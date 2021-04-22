package com.laputa.plugin.logger.transform

import com.laputa.plugin.logger.line1
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

/**
 * Author by xpl, Date on 2021/4/21.
 */
class LoggerAdviceAdapter(
    methodVisitor: MethodVisitor, access: Int, name: String?,
    descriptor: String?
) : AdviceAdapter(
    Opcodes.ASM5, methodVisitor,
    access,
    name, descriptor
) {

    companion object {
        private const val TAG = "laputa_logger"

        // 注解descriptor
        private const val DESCRIPTOR_ANNOTATION = "Lcom/laputa/plan/logger/Logger;"
        private const val OWNER_LOGGER = "com/laputa/plan/logger/TimeCache"
    }

    private var inject: Boolean = false

    override fun visitCode() {
        super.visitCode()
        line1("               -> LoggerAdviceAdapter::visitCode")
    }

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        line1("               -> LoggerAdviceAdapter::visitAnnotation ${descriptor}")
        if (DESCRIPTOR_ANNOTATION == descriptor
        // todo 怎么直接用注解类型？
        // || Type.getDescriptor(com.laputa.annotations.Dog::class.java) == descriptor
        // todo 只能用这种方式么？如何获取注解上的信息？
        // || "Lcom/laputa/annotations/Dog;" == descriptor
        // || Type.getDescriptor(Cat::class.java) == descriptor
        ) {
            inject = true
        }
        return super.visitAnnotation(descriptor, visible)
    }

    override fun onMethodEnter() {
        super.onMethodEnter()
        line1("               -> LoggerAdviceAdapter::onMethodEnter ")
        if (inject) {

            mv.visitFieldInsn(
                GETSTATIC,
                "java/lang/System",
                "out",
                "Ljava/io/PrintStream;"
            )
            mv.visitLdcInsn("========start=========$name==>des:")
            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                "java/io/PrintStream",
                "println",
                "(Ljava/lang/String;)V",
                false
            );

            mv.visitLdcInsn(name);
            mv.visitMethodInsn(
                INVOKESTATIC,
                "java/lang/System",
                "currentTimeMillis",
                "()J",
                false
            )
            mv.visitMethodInsn(
                INVOKESTATIC,
                OWNER_LOGGER,
                "setStartTime",
                "(Ljava/lang/String;J)V",
                false
            );
        }
    }

    override fun onMethodExit(opcode: Int) {
        super.onMethodExit(opcode)
        line1("               -> LoggerAdviceAdapter::onMethodExit")
        if (inject) {
            mv.visitLdcInsn(name);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
            mv.visitMethodInsn(
                INVOKESTATIC, OWNER_LOGGER, "setEndTime",
                "(Ljava/lang/String;J)V", false
            );

            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitLdcInsn(name)
            mv.visitMethodInsn(
                INVOKESTATIC, OWNER_LOGGER, "getCostTime",
                "(Ljava/lang/String;)Ljava/lang/String;", false
            );
            mv.visitMethodInsn(
                INVOKEVIRTUAL, "java/io/PrintStream", "println",
                "(Ljava/lang/String;)V", false
            );

            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitLdcInsn("========end=========");
            mv.visitMethodInsn(
                INVOKEVIRTUAL, "java/io/PrintStream", "println",
                "(Ljava/lang/String;)V", false
            );
        }
    }

    override fun visitEnd() {
        super.visitEnd()

    }
}