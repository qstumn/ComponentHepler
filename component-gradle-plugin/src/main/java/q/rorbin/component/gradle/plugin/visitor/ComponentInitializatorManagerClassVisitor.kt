package q.rorbin.component.gradle.plugin.visitor

import jdk.internal.org.objectweb.asm.*
import jdk.internal.org.objectweb.asm.commons.AdviceAdapter
import q.rorbin.component.ComponentInitializatorManager

/**
 * @author changhai.qiu
 */
class ComponentInitializatorManagerClassVisitor(private val classVisitor: ClassVisitor, private val helpers: MutableList<String>) : ClassVisitor(Opcodes.ASM5, classVisitor) {

    override fun visitMethod(
            access: Int,
            name: String,
            descriptor: String,
            signature: String?,
            exceptions: Array<out String>?
    ): MethodVisitor {
        var methodVisitor = classVisitor.visitMethod(access, name, descriptor, signature, exceptions)
        if (name == "addAllInitializator") {
            methodVisitor = AddAllInitializatorMethodAdapter(methodVisitor, access, name, descriptor)
        }
        return methodVisitor
    }

    private inner class AddAllInitializatorMethodAdapter(mv: MethodVisitor, access: Int, private val name: String?, desc: String?)
        : AdviceAdapter(Opcodes.ASM5, mv, access, name, desc) {

        override fun onMethodEnter() {
            super.onMethodEnter()
            for (helper in helpers) {
                mv.visitVarInsn(Opcodes.ALOAD, 0)
                mv.visitLdcInsn(helper)
                mv.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(ComponentInitializatorManager::class.java),
                        "addInitializator", "(Ljava/lang/String;)V", false)
            }
        }
    }
}
