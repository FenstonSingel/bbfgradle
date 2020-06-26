package com.stepanov.bbf

import com.stepanov.bbf.bugfinder.isolation.BugIsolator
import com.stepanov.bbf.bugfinder.manager.BugType
import com.stepanov.bbf.coverage.CompilerInstrumentation
import com.stepanov.bbf.coverage.ExecutionCoverage
import org.apache.log4j.PropertyConfigurator

fun main() {

    PropertyConfigurator.configure("src/main/resources/bbfLog4j.properties")
    PropertyConfigurator.configure("src/main/resources/reduktorLog4j.properties")

    BugIsolator.isolate("/home/fenstonsingel/kotlin-samples/set-a/3/BACKEND_bhugqgy_FILE.kt", BugType.BACKEND)

    println("Isolations: ${BugIsolator.numberOfIsolations}")
    println("Total: ${BugIsolator.totalIsolationTime}")
    println("Average: ${BugIsolator.averageIsolationTime}")
    println("Compilations: ${BugIsolator.numberOfCompilations}")
    println("Total instrumentation time: ${CompilerInstrumentation.instrumentationTimer}")
    println("Total instrumentation effect: ${BugIsolator.totalPerformanceTime}")
    println("Average instrumentation effect: ${BugIsolator.averagePerformanceTime}")

    BugIsolator.isolate("/home/fenstonsingel/kotlin-samples/set-a/3/BACKEND_pytmh.kt", BugType.BACKEND)

    println("Isolations: ${BugIsolator.numberOfIsolations}")
    println("Total: ${BugIsolator.totalIsolationTime}")
    println("Average: ${BugIsolator.averageIsolationTime}")
    println("Compilations: ${BugIsolator.numberOfCompilations}")
    println("Total instrumentation time: ${CompilerInstrumentation.instrumentationTimer}")
    println("Total instrumentation effect: ${BugIsolator.totalPerformanceTime}")
    println("Average instrumentation effect: ${BugIsolator.averagePerformanceTime}")

}