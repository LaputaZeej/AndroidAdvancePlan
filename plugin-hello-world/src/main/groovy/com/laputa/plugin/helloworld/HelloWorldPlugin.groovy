package com.laputa.plugin.helloworld

import org.gradle.api.Plugin
import org.gradle.api.Project

class HelloWorldPlugin implements Plugin<Project> {

    @Override
    void apply(Project target) {
        println("************ HelloWorldTransform ************ ")
//        target.task("a_hello_world_test_01"){
//            doFirst {
//                println(" ************ hello world ************ ")
//                println("-> version" + target.version)
//            }
//        }
        target.gradle.addListener(new TimeListener())

    }



}