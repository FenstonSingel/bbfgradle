package com.stepanov.bbf

import com.stepanov.bbf.bugfinder.isolation.BugIsolator
import com.stepanov.bbf.bugfinder.isolation.formulas.Ochiai2RankingFormula
import com.stepanov.bbf.bugfinder.manager.BugType
import com.stepanov.bbf.coverage.CompilerInstrumentation
import org.apache.log4j.PropertyConfigurator

fun main() {

    PropertyConfigurator.configure("src/main/resources/bbfLog4j.properties")
    PropertyConfigurator.configure("src/main/resources/reduktorLog4j.properties")

    BugIsolator.isolate(
            "/home/fenstonsingel/kotlin-samples/set-a/3/BACKEND_bhugqgy_FILE.kt",
            BugType.BACKEND, Ochiai2RankingFormula
    )


    println("Isolations: ${BugIsolator.numberOfIsolations}")
    println("Total isolation time: ${BugIsolator.totalIsolationTime}")
    println("Average isolation time: ${BugIsolator.averageIsolationTime}")
    println("Compilations: ${BugIsolator.numberOfCompilations}")
    println("Time spent on instrumentation: ${CompilerInstrumentation.instrumentationTimer}")
    println("Total coverage recording time: ${BugIsolator.totalPerformanceTime}")
    println("Average coverage recording time: ${BugIsolator.averagePerformanceTime}")

    BugIsolator.isolate(
            "/home/fenstonsingel/kotlin-samples/set-a/3/BACKEND_pytmh.kt",
            BugType.BACKEND, Ochiai2RankingFormula
    )

    println("Isolations: ${BugIsolator.numberOfIsolations}")
    println("Total isolation time: ${BugIsolator.totalIsolationTime}")
    println("Average isolation time: ${BugIsolator.averageIsolationTime}")
    println("Compilations: ${BugIsolator.numberOfCompilations}")
    println("Time spent on instrumentation: ${CompilerInstrumentation.instrumentationTimer}")
    println("Total coverage recording time: ${BugIsolator.totalPerformanceTime}")
    println("Average coverage recording time: ${BugIsolator.averagePerformanceTime}")

}