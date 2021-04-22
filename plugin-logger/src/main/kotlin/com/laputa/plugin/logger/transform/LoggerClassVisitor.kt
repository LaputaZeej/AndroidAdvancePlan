package com.laputa.plugin.logger.transform

import com.laputa.plugin.logger.line1
//import jdk.internal.org.objectweb.asm.ClassVisitor
//import jdk.internal.org.objectweb.asm.MethodVisitor
//import jdk.internal.org.objectweb.asm.Opcodes
// todo 注意包
import org.objectweb.asm.Opcodes
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor


/**
 * Author by xpl, Date on 2021/4/21.
 */
class LoggerClassVisitor(private val type: Boolean = true, classVisitor: ClassVisitor) :
    ClassVisitor(Opcodes.ASM5, classVisitor) {
    private var className: String? = null

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        super.visit(version, access, name, signature, superName, interfaces)
        className = name
        line1("           -> LoggerClassVisitor::visit")
        line1("           -> className = $className")
    }


    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        line1("           -> LoggerClassVisitor::visitMethod name = $name,descriptor=$descriptor type = $type ")
        val visitMethod = super.visitMethod(access, name, descriptor, signature, exceptions)
        // 方式一
        if (type) {
            return LoggerAdviceAdapter(visitMethod, access, name, descriptor)
        }

        // 方式二
        // 过滤方法 ：className 要是MainActivity 并且方法是onCreate或者testAsm
        if (className?.endsWith("MainActivity") == true
            && (name == "onCreate" || name == "testAsm")
        ) {//过滤需要操作的类名和方法名
            return LoggerMethodVisitor(visitMethod)
        }

        return visitMethod
    }
}