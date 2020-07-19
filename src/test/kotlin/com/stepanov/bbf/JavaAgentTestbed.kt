package com.stepanov.bbf

import com.stepanov.bbf.coverage.CompilerInstrumentation
import com.stepanov.bbf.coverage.ProgramCoverage
import org.jetbrains.kotlin.TestKotlinClass

// TODO Better and automated Java agemt tests.

fun run(code: () -> Unit) {
    CompilerInstrumentation.clearRecords()

    CompilerInstrumentation.shouldProbesBeRecorded = true
    code()
    CompilerInstrumentation.shouldProbesBeRecorded = false

    val coverage = ProgramCoverage.createFromProbes()
    val entities = coverage.entities()
    for (entity in entities) {
        println("$entity: ${coverage[entity]}")
    }

    println("Instrumentation time: ${CompilerInstrumentation.timeSpentOnInstrumentation}")
    println("Performance time: ${CompilerInstrumentation.instrumentationPerformanceTime}")
    println()
}

fun main() {
    var threshold = 0
    var total = 1000000

    val code = {
        val testClass = TestKotlinClass()
        for (i in 0 until total) {
            if (i < threshold) testClass.foo(i) else testClass.bar(i)
        }
    }

    threshold = 5000
    run(code)

    threshold = 100000
    run(code)

    val testClass = TestKotlinClass()
    val newCode = {
        for (i in 0 until total) {
            if (i < threshold) testClass.foo(i) else testClass.bar(i)
        }
    }

    total = 100000

    threshold = 5000
    run(newCode)

    threshold = 100000
    run(newCode)
}