package com.stepanov.bbf

import com.stepanov.bbf.bugfinder.isolation.BugIsolator
import com.stepanov.bbf.bugfinder.manager.BugType
import org.apache.log4j.PropertyConfigurator

fun main() {

    PropertyConfigurator.configure("src/main/resources/bbfLog4j.properties")
    PropertyConfigurator.configure("src/main/resources/reduktorLog4j.properties")

    BugIsolator.isolate("/home/fenstonsingel/kotlin-samples/set-a/3/BACKEND_bhugqgy_FILE.kt", BugType.BACKEND)
    BugIsolator.isolate("/home/fenstonsingel/kotlin-samples/set-a/3/BACKEND_pytmh.kt", BugType.BACKEND)

}