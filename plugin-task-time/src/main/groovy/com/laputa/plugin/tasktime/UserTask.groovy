package com.laputa.plugin.tasktime

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class UserTask extends DefaultTask {
    @TaskAction
    void output() {
        println("${TaskTimePlugin.TAG} user.name = ${project.laputa_user.name} ${TaskTimePlugin.TAG}")
        println("${TaskTimePlugin.TAG} user.email = ${project.laputa_user.email} ${TaskTimePlugin.TAG}")
    }
}