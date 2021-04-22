package com.laputa.plugin.logger

import com.android.build.gradle.AppExtension
import com.laputa.plugin.logger.transform.LoggerTransform
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Author by xpl, Date on 2021/4/21.
 */
class LoggerPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        line(" LoggerPlugin")
        line1("version = ${target.version}")
        target.extensions.apply {
                this.create("laputa_logger",LoggerExtension::class.java)
                getByType(AppExtension::class.java)
                .registerTransform(LoggerTransform(target))
        }

        line1("....end")
    }
}