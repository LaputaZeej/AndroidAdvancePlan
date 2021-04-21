package com.laputa.plugin.tasktime

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class AddressTask extends DefaultTask {
    @TaskAction
    void output() {
        println("${TaskTimePlugin.TAG} user.name = ${project.laputa_user.name} ${TaskTimePlugin.TAG}")
        println("${TaskTimePlugin.TAG} user.email = ${project.laputa_user.email} ${TaskTimePlugin.TAG}")

        println("${TaskTimePlugin.TAG} address.province = ${project.laputa_user.laputa_address.province} ${TaskTimePlugin.TAG}"  )
        println("${TaskTimePlugin.TAG} address.city = ${project.laputa_user.laputa_address.city} ${TaskTimePlugin.TAG}"  )
        println("${TaskTimePlugin.TAG} address.area = ${project.laputa_user.laputa_address.area} ${TaskTimePlugin.TAG}"  )
    }
}