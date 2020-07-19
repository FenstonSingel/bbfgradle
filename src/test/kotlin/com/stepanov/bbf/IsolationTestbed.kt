package com.stepanov.bbf

import com.stepanov.bbf.bugfinder.isolation.BugIsolator
import com.stepanov.bbf.bugfinder.isolation.formulas.*
import com.stepanov.bbf.bugfinder.manager.BugType
import com.stepanov.bbf.coverage.CompilerInstrumentation
import org.apache.log4j.PropertyConfigurator

fun main() {

    PropertyConfigurator.configure("src/main/resources/bbfLog4j.properties")
    PropertyConfigurator.configure("src/main/resources/reduktorLog4j.properties")

    BugIsolator.rankingFormula = Ochiai2RankingFormula


    BugIsolator.isolate("/home/fenstonsingel/kotlin-samples/set-a/3/BACKEND_bhugqgy_FILE.kt", BugType.BACKEND)

    println("Isolations: ${BugIsolator.numberOfIsolations}")
    println("Total isolation time: ${BugIsolator.totalIsolationTime}")
    println("Average isolation time: ${BugIsolator.meanIsolationTime}")
    println("Compilations: ${BugIsolator.numberOfCompilations}")
    println("Time spent on instrumentation: ${CompilerInstrumentation.timeSpentOnInstrumentation}")
    println("Total coverage recording time: ${BugIsolator.totalInstrPerformanceTime}")
    println("Average coverage recording time: ${BugIsolator.meanInstrPerformanceTime}")

    BugIsolator.isolate("/home/fenstonsingel/kotlin-samples/set-a/3/BACKEND_pytmh.kt", BugType.BACKEND)

    println("Isolations: ${BugIsolator.numberOfIsolations}")
    println("Total isolation time: ${BugIsolator.totalIsolationTime}")
    println("Average isolation time: ${BugIsolator.meanIsolationTime}")
    println("Compilations: ${BugIsolator.numberOfCompilations}")
    println("Time spent on instrumentation: ${CompilerInstrumentation.timeSpentOnInstrumentation}")
    println("Total coverage recording time: ${BugIsolator.totalInstrPerformanceTime}")
    println("Average coverage recording time: ${BugIsolator.meanInstrPerformanceTime}")

}