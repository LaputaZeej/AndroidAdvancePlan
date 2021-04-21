package com.laputa.plugin.tasktime

import com.laputa.plugin.tasktime.extension.AddressExtension
import com.laputa.plugin.tasktime.extension.UserExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class TaskTimePlugin implements Plugin<Project> {
    static def TAG = "%%%%%%%%%%%%%%%%"

    @Override
    void apply(Project target) {
        target.task("a_task_time") {
            println("$TAG TaskTimePlugin::a_task_time $TAG")
        }
        target.gradle.addListener(new TimeListener())

        // 获取gradle参数

        target.extensions.create("laputa_user", UserExtension)
        target.task("a_task_time_extension_user", type: UserTask)

        target.laputa_user.extensions.create("laputa_address", AddressExtension)
        target.task("a_task_time_extension_user_extra", type: AddressTask)


    }
}