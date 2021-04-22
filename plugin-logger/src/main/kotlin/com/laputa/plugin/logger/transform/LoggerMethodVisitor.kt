package com.laputa.plugin.logger.transform

import com.laputa.plugin.logger.line1
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes


/**
 * Author by xpl, Date on 2021/4/21.
 */
class LoggerMethodVisitor(methodVisitor: MethodVisitor) :
    MethodVisitor(Opcodes.ASM5, methodVisitor) {

    companion object {
        private const val TAG = "laputa_logger"
    }

    override fun visitCode() {
        super.visitCode()
        line1("               -> LoggerMethodVisitor::visitMethod")
        mv.visitLdcInsn(TAG)
        mv.visitLdcInsn("hello ASM !!! ");
        mv.visitMethodInsn(
            Opcodes.INVOKESTATIC,
            "android/util/Log",
            "i",
            "(Ljava/lang/String;Ljava/lang/String;)I",
            false
        );
        mv.visitInsn(org.objectweb.asm.Opcodes.POP)

    }

    //指令操作,这里可以判断拦截return,并在方法尾部插入字节码
    override fun visitInsn(opcode: Int) {
        println("               -> LoggerMethodVisitor::visitInsn : opcode = $opcode")
        if (opcode == Opcodes.ARETURN || opcode == Opcodes.RETURN) {
            // android.util.Log.i("MainActivity", "ttt run3")
            mv.visitLdcInsn(TAG);
            mv.visitLdcInsn("hello asm after");
            mv.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "android/util/Log",
                "i",
                "(Ljava/lang/String;Ljava/lang/String;)I",
                false
            );
            mv.visitInsn(Opcodes.POP);
        }
        super.visitInsn(opcode)
    }

    override fun visitMaxs(maxStack: Int, maxLocals: Int) {
        super.visitMaxs(maxStack, maxLocals)
    }

    override fun visitEnd() {
        super.visitEnd()
        line1("               -> LoggerMethodVisitor::visitEnd :)")
    }

}