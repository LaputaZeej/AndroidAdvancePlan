package com.laputa.plugin.tasktime

import org.gradle.BuildListener
import org.gradle.BuildResult
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
import org.gradle.api.tasks.TaskState

class TimeListener implements TaskExecutionListener, BuildListener {

    private long current
    private times = []

    @Override
    void beforeExecute(Task task) {
        current = System.currentTimeMillis()
    }

    @Override
    void afterExecute(Task task, TaskState state) {
        def ms = System.currentTimeMillis() - current
        times.add([ms, task.path])
    }

    @Override
    void buildFinished(BuildResult result) {
        println "<<<<<<<<<<<<<<<<<<< buildFinished time a >>>>>>>>>>>>>>>>>>>"
        for (time in times) {
            if (time[0] > 50) {
                printf "%7sms %s\n", time
            }
        }
        println "<<<<<<<<<<<<<<<<<<< buildFinished time z >>>>>>>>>>>>>>>>>>>"
    }

    @Override
    void buildStarted(Gradle gradle) {

    }

    @Override
    void settingsEvaluated(Settings settings) {

    }

    @Override
    void projectsLoaded(Gradle gradle) {

    }

    @Override
    void projectsEvaluated(Gradle gradle) {

    }


}