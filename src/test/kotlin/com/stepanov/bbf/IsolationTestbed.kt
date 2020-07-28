package com.stepanov.bbf

import com.stepanov.bbf.bugfinder.isolation.BugIsolator
import com.stepanov.bbf.bugfinder.isolation.formulas.*
import com.stepanov.bbf.bugfinder.manager.BugType
import com.stepanov.bbf.coverage.CompilerInstrumentation
import org.apache.log4j.PropertyConfigurator

fun statistics() {
    println("Isolations: ${BugIsolator.numberOfIsolations}")
    println("Total isolation time: ${BugIsolator.totalIsolationTime}")
    println("Average isolation time: ${BugIsolator.meanIsolationTime}")
    println()
    println("Compilations: ${BugIsolator.numberOfCompilations}")
    println("Time spent on instrumentation: ${CompilerInstrumentation.timeSpentOnInstrumentation}")
    println("Total coverage recording time: ${BugIsolator.totalInstrPerformanceTime}")
    println("Average coverage recording time: ${BugIsolator.meanInstrPerformanceTime}")
    println()
    println("Total failing code samples generated: ${BugIsolator.totalFailingCodeSamples}")
    println("Average failing code samples generated: ${BugIsolator.meanFailingCodeSamples}")
    println("Total passing code samples generated: ${BugIsolator.totalPassingCodeSamples}")
    println("Average passing code samples generated: ${BugIsolator.meanPassingCodeSamples}")
    println()
    println("Distribution of samples per mutation:")
    BugIsolator.codeSampleDistributionPerMutation.toSortedMap().forEach { key, (bugsRatio, successesRatio) ->
        println("   $key: %.2f%% fails and %.2f%% passes".format(bugsRatio * 100, successesRatio * 100))
    }
    println()
    println()
}

fun main() {

    PropertyConfigurator.configure("src/main/resources/bbfLog4j.properties")
    PropertyConfigurator.configure("src/main/resources/reduktorLog4j.properties")

    BugIsolator.rankingFormula = Ochiai2RankingFormula


    val ranking1 = BugIsolator.isolate("/home/fenstonsingel/kotlin-samples/set-a/3/BACKEND_bhugqgy_FILE.kt", BugType.BACKEND)
    statistics()

    val ranking2 = BugIsolator.isolate("/home/fenstonsingel/kotlin-samples/set-a/6/BACKEND_dooqtxk_FILE.kt", BugType.BACKEND)
    statistics()

    println(ranking1.cosineSimilarity(ranking2))
    println()
    println()

    val ranking3 = BugIsolator.isolate("/home/fenstonsingel/kotlin-samples/set-a/3/BACKEND_pytmh.kt", BugType.BACKEND)
    statistics()

    println(ranking1.cosineSimilarity(ranking3))
    println()
    println()

}