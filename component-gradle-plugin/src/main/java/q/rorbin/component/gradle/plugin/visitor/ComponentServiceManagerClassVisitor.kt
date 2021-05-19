package q.rorbin.component.gradle.plugin.visitor

import jdk.internal.org.objectweb.asm.*
import jdk.internal.org.objectweb.asm.commons.AdviceAdapter
import q.rorbin.component.ComponentServiceManager

/**
 * @author changhai.qiu
 */
class ComponentServiceManagerClassVisitor(
    private val classVisitor: ClassVisitor,
    private val helpers: MutableList<String>
) : ClassVisitor(Opcodes.ASM5, classVisitor) {

    override fun visitMethod(
        access: Int,
        name: String,
        descriptor: String,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor? {
        var methodVisitor =
            classVisitor.visitMethod(access, name, descriptor, signature, exceptions)
        if (name == "initServices") {
            methodVisitor = InitServicesMethodAdapter(methodVisitor)
        }
        return methodVisitor
    }

    private inner class InitServicesMethodAdapter(val visitor: MethodVisitor) :
        MethodVisitor(Opcodes.ASM5) {
        override fun visitCode() {
            for (helper in helpers) {
                visitor.visitVarInsn(Opcodes.ALOAD, 0)
                visitor.visitLdcInsn(helper)
                visitor.visitMethodInsn(
                    Opcodes.INVOKESPECIAL, Type.getInternalName(
                        ComponentServiceManager::class.java
                    ),
                    "initServiceByHelper", "(Ljava/lang/String;)V", false
                )
            }
            visitor.visitInsn(Opcodes.RETURN)
        }
    }
}
