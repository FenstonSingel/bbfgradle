package com.stepanov.bbf

import com.stepanov.bbf.coverage.CompilerInstrumentation
import org.jetbrains.kotlin.TestKotlinClass

fun run(code: () -> Unit) {
    CompilerInstrumentation.clearRecords()
    CompilerInstrumentation.shouldProbesBeRecorded = true
    code()
    CompilerInstrumentation.shouldProbesBeRecorded = false
    println(CompilerInstrumentation.entryProbes)
    println(CompilerInstrumentation.branchProbes)
    println("Instrumentation time: ${CompilerInstrumentation.instrumentationTimer}")
    println("Performance time: ${CompilerInstrumentation.performanceTimer}")
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