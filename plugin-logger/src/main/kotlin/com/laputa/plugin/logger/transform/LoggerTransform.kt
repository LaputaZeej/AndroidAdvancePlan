package com.laputa.plugin.logger.transform

import com.android.build.api.transform.Format
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import com.laputa.plugin.logger.LoggerExtension
import com.laputa.plugin.logger.line
import com.laputa.plugin.logger.line1
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import java.io.FileOutputStream

/**
 * Author by xpl, Date on 2021/4/21.
 */
class LoggerTransform(private val project: Project) : Transform() {
    private var type = 1

    override fun getName(): String = "com.laputa.plugin.logger.transform.LoggerTransform"

    //transform要处理的输入类型,有class,resource,dex
    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> =
        TransformManager.CONTENT_CLASS

    /**
     * 输入文件的范围
     * PROJECT 当前工程
     * SUB_PROJECTS 子工程
     * EXTERNAL_LIBRARIES lib
     * LOCAL_DEPS jar
     */
    override fun getScopes(): MutableSet<in QualifiedContent.Scope> =
        TransformManager.SCOPE_FULL_PROJECT

    override fun isIncremental(): Boolean = false

    override fun transform(transformInvocation: TransformInvocation?) {
        super.transform(transformInvocation)
        line("LoggerTransform a")

        // val type = project.laputa_logger.type
        type = try {
            project.extensions.getByType(LoggerExtension::class.java).type.toInt()
        } catch (e: Throwable) {
            0
        }
        line1(" type = $type")

        val inputs = transformInvocation?.inputs
        val outputProvider = transformInvocation?.outputProvider

        if (!isIncremental) {
            outputProvider?.deleteAll()
        }

        inputs?.forEach { input ->
            line1("input${input}")
            input.directoryInputs.forEach {
                line1("   directoryInput=${it.name}")
                if (it.file.isDirectory) {
                    FileUtils.getAllFiles(it.file).forEach { file ->
                        line1("       file=${file.name}")
                        val name = file.name
                        //过滤出需要的class,将一些基本用不到的class去掉
                        if (name.endsWith(".class") && name != "R.class"
                            && !name.startsWith("R\$") && name != ("BuildConfig.class")
                        ) {
                            val classPath = file.absoluteFile
                            line1("       file#classPath=${classPath}")
                            val cr = ClassReader(file.readBytes())
                            val cw = ClassWriter(cr, ClassWriter.COMPUTE_MAXS)
                            //需要处理的类使用自定义的visitor来处理
                            val visitor = LoggerClassVisitor(type == 0, cw)
                            cr.accept(visitor, ClassReader.EXPAND_FRAMES)
                            val bytes = cw.toByteArray()
                            val fos = FileOutputStream(classPath)
                            fos.write(bytes)
                            fos.close()
                        }
                    }
                }

                val dest = outputProvider?.getContentLocation(
                    it.name,
                    it.contentTypes,
                    it.scopes,
                    Format.DIRECTORY
                )
                FileUtils.copyDirectoryToDirectory(it.file, dest)
            }

            //将jar也加进来,androidx需要这个
            input.jarInputs.forEach {
                val dest = outputProvider?.getContentLocation(
                    it.name,
                    it.contentTypes,
                    it.scopes,
                    Format.JAR
                )
                FileUtils.copyFile(it.file, dest)
            }
        }
        line("LoggerTransform z")
    }
}