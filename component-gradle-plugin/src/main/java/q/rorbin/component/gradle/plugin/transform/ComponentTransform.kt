package q.rorbin.component.gradle.plugin.transform

import com.android.build.api.transform.*
import com.android.utils.FileUtils
import org.apache.commons.codec.digest.DigestUtils
import q.rorbin.component.ComponentInitializatorManager
import q.rorbin.component.ComponentServiceManager
import q.rorbin.component.annotation.ComponentInitializator
import q.rorbin.component.annotation.ComponentService
import q.rorbin.component.gradle.plugin.common.*
import java.io.File
import java.util.jar.JarFile

/**
 * @author changhai.qiu
 */
class ComponentTransform : Transform() {
    override fun getName(): String = "monph-component-transform"

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> =
        mutableSetOf(QualifiedContent.DefaultContentType.CLASSES)

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> = mutableSetOf(
        QualifiedContent.Scope.PROJECT, QualifiedContent.Scope.SUB_PROJECTS,
        QualifiedContent.Scope.EXTERNAL_LIBRARIES
    )

    override fun isIncremental(): Boolean = true

    override fun transform(transformInvocation: TransformInvocation?) {
        super.transform(transformInvocation)
        transformInvocation ?: return

        val serviceHelpers = mutableListOf<String>()
        val initializatorHelpers = mutableListOf<String>()
        var serviceHelperManager: File? = null
        var initializatorHelperManager: File? = null

        val inputs = transformInvocation.inputs
        val outputProvider = transformInvocation.outputProvider

        if (!transformInvocation.isIncremental) {
            outputProvider.deleteAll()
        }

        val servicePackage = "${ComponentService::class.java.`package`.name}."
        val initializatorPackage = "${ComponentInitializator::class.java.`package`.name}."

        inputs.forEach { input ->
            //遍历所有的文件夹收集serviceHelpers和initializatorHelpers
            input.directoryInputs.forEach { dirInput ->
                val inputDir = dirInput.file
                val destDir = outputProvider.getContentLocation(
                    dirInput.name,
                    dirInput.contentTypes,
                    dirInput.scopes,
                    Format.DIRECTORY
                )
                inputDir.traverse(
                    { file -> file.extension == "class" },
                    FileType.FILE
                ) { file ->
                    val name = file.name.replace(".class", "")
                    if (isServiceHelper(name)) {
                        serviceHelpers.add("$servicePackage$name")
                    }
                    if (isInitializatorHelper(name)) {
                        initializatorHelpers.add("$initializatorPackage$name")
                    }
                }
                if (transformInvocation.isIncremental) {
                    for ((file, status) in dirInput.changedFiles) {
                        val destFile =
                            File(destDir, FileUtils.relativePossiblyNonExistingPath(file, inputDir))
                        when (status) {
                            Status.ADDED, Status.CHANGED -> FileUtils.copyFile(file, destFile)
                            Status.REMOVED -> FileUtils.delete(destFile)
                        }
                    }
                } else {
                    FileUtils.copyDirectory(inputDir, destDir)
                }
            }
            //遍历所有的jar文件收集serviceHelpers和initializatorHelpers, 并记录当前serviceHelperManager或initializatorHelperManager是否在这个jar中
            input.jarInputs.forEach { jarInput ->
                val dest = outputProvider.getContentLocation(
                    jarInput.name,
                    jarInput.contentTypes,
                    jarInput.scopes,
                    Format.JAR
                )
                if (shouldProcessPreDexJar(jarInput.file.absolutePath)) {
                    val file = JarFile(jarInput.file)
                    val enumeration = file.entries()
                    while (enumeration.hasMoreElements()) {
                        val jarEntry = enumeration.nextElement()
                        val name = jarEntry.name.replace(".class", "").replace("\\", ".")
                            .replace("/", ".")
                        var simpleName = name.substring(name.lastIndexOf(".") + 1)
                        if (simpleName.contains("$")) {
                            simpleName = simpleName.substring(simpleName.lastIndexOf("$") + 1)
                        }
                        if (name == ComponentServiceManager::class.java.name) {
                            serviceHelperManager = dest
                        }
                        if (name == ComponentInitializatorManager::class.java.name) {
                            initializatorHelperManager = dest
                        }
                        if (isServiceHelper(simpleName)) {
                            serviceHelpers.add(name)
                        }
                        if (isInitializatorHelper(simpleName)) {
                            initializatorHelpers.add(name)
                        }
                    }
                }
                if (transformInvocation.isIncremental) {
                    when (jarInput.status) {
                        Status.ADDED, Status.CHANGED -> FileUtils.copyFile(jarInput.file, dest)
                        Status.REMOVED -> FileUtils.delete(dest)
                    }
                } else {
                    FileUtils.copyFile(jarInput.file, dest)
                }
            }
        }
        println("serviceHelpers gather result : $serviceHelpers, serviceHelperManager : $serviceHelperManager")
        println("initializatorHelpers gather result : $initializatorHelpers, initializatorHelperManager : $initializatorHelperManager")
        if (serviceHelpers.isNotEmpty() && serviceHelperManager != null) {
            processServiceManager(serviceHelpers, serviceHelperManager!!)
        }
        if (initializatorHelpers.isNotEmpty() && initializatorHelperManager != null) {
            processInitializatorManager(initializatorHelpers, initializatorHelperManager!!)
        }
    }
}