package q.rorbin.component.gradle.plugin.common

import jdk.internal.org.objectweb.asm.ClassReader
import jdk.internal.org.objectweb.asm.ClassVisitor
import jdk.internal.org.objectweb.asm.ClassWriter
import q.rorbin.component.ComponentConst
import q.rorbin.component.ComponentInitializatorManager
import q.rorbin.component.ComponentServiceManager
import q.rorbin.component.gradle.plugin.visitor.ComponentInitializatorManagerClassVisitor
import q.rorbin.component.gradle.plugin.visitor.ComponentServiceManagerClassVisitor
import java.io.File
import java.io.FileOutputStream
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry


/**
 * @author changhai.qiu
 */

fun isServiceHelper(name: String): Boolean {
    return name.startsWith(ComponentConst.PREFIX) && name.endsWith(ComponentConst.SERVICE_HEPLER_SUFFIX)
}

fun isInitializatorHelper(name: String): Boolean {
    return name.startsWith(ComponentConst.PREFIX) && name.endsWith(ComponentConst.INITIALIZATOR_HEPLER_SUFFIX)
}

fun shouldProcessPreDexJar(path: String): Boolean {
    return !path.contains("com.android.support") && !path.contains("/android/m2repository")
}

fun processServiceManager(
    serviceHelpers: MutableList<String>,
    serviceHelperManagerJar: File
) {
    processManager(serviceHelperManagerJar, ComponentServiceManager::class.java.name) {
        ComponentServiceManagerClassVisitor(it, serviceHelpers)
    }
}

fun processInitializatorManager(
    initializatorHelpers: MutableList<String>,
    initializatorHelperManagerJar: File
) {
    processManager(initializatorHelperManagerJar, ComponentInitializatorManager::class.java.name) {
        ComponentInitializatorManagerClassVisitor(it, initializatorHelpers)
    }
}

private fun processManager(
    managerJar: File,
    managerClassName: String,
    getVisitor: (ClassWriter) -> ClassVisitor
) {
    val jarFile = JarFile(managerJar)
    //创建一个新的jar文件用于替换原本的jar
    val newJar = File(managerJar.parent, "new_${managerJar.name}")
    if (newJar.exists()) {
        newJar.delete()
    }
    val jarOs = JarOutputStream(FileOutputStream(newJar))
    val enumeration = jarFile.entries()
    while (enumeration.hasMoreElements()) {
        val jarEntry = enumeration.nextElement()
        val enrtyIs = jarFile.getInputStream(jarEntry)
        jarOs.putNextEntry(ZipEntry(jarEntry.name))
        val className = jarEntry.name.replace(".class", "")
            .replace("\\", ".")
            .replace("/", ".")
        if (className == managerClassName) {
            println("finded class : ${jarEntry.name}")
            //使用ASM修改Component$Manager
            val classReader = ClassReader(enrtyIs)
            val classWriter = ClassWriter(ClassWriter.COMPUTE_MAXS)
            val visitor = getVisitor(classWriter)
            classReader.accept(visitor, ClassReader.EXPAND_FRAMES)
            val bytes = classWriter.toByteArray()
            jarOs.write(bytes)
        } else {
            //其他类原样放入jar
            jarOs.write(enrtyIs.readBytes())
        }
        enrtyIs.close()
        jarOs.closeEntry()
    }
    jarOs.close()
    jarFile.close()
    //将原本的已经到dest的jar删除
    if (managerJar.exists()) {
        managerJar.delete()
    }
    //将新的jar文件替换dest的原本的jar
    newJar.renameTo(managerJar)
}
