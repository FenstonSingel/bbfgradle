package com.stepanov.bbf.coverage

import com.stepanov.bbf.coverage.impl.BranchBasedCoverage
import com.stepanov.bbf.coverage.impl.MethodBasedCoverage
import org.jetbrains.kotlin.cli.AnotherUselessTestKlass
import org.jetbrains.kotlin.fir.RelevantTestKlass
import org.jetbrains.not.kotlin.UselessTestKlass
import kotlin.test.assertEquals

fun main() {
    when (CompilerInstrumentation.coverageType) {
        CompilerInstrumentation.CoverageType.METHOD -> {
            checkRecords(
                MethodBasedCoverage(mapOf(
                    "org/jetbrains/kotlin/fir/RelevantTestKlass:<init>()V" to 1,
                    "org/jetbrains/kotlin/fir/RelevantTestKlass:testMethodCoverage1()V" to 75,
                    "org/jetbrains/kotlin/fir/RelevantTestKlass:testMethodCoverage2()V" to 25
                ))
            ) {
                val o = RelevantTestKlass()
                for (i in 0 until 100) {
                    if (i < 75) o.testMethodCoverage1() else o.testMethodCoverage2()
                }
            }
        }
        CompilerInstrumentation.CoverageType.BRANCH -> {
            checkRecords(
                BranchBasedCoverage(mapOf(
                    "org/jetbrains/kotlin/fir/RelevantTestKlass:testUnaryRefCmpCoverage(Ljava/lang/Object;)V:IFNONNULL1" to BranchBasedCoverage.BranchProbesResults(mapOf("A" to 5, "B" to 5)),
                    "org/jetbrains/kotlin/fir/RelevantTestKlass:testUnaryRefCmpCoverage(Ljava/lang/Object;)V:IFNULL1" to BranchBasedCoverage.BranchProbesResults(mapOf("A" to 5, "B" to 5)),
                    "org/jetbrains/kotlin/fir/RelevantTestKlass:testBinaryRefCmpCoverage(Ljava/lang/Object;)V:IF_ACMPNE1" to BranchBasedCoverage.BranchProbesResults(mapOf("A" to 10)),
                    "org/jetbrains/kotlin/fir/RelevantTestKlass:testBinaryRefCmpCoverage(Ljava/lang/Object;)V:IF_ACMPEQ1" to BranchBasedCoverage.BranchProbesResults(mapOf("A" to 10)),
                    "org/jetbrains/kotlin/fir/RelevantTestKlass:testIntCmpCoverage(I)V:IFLT1" to BranchBasedCoverage.BranchProbesResults(mapOf("B" to 10)),
                    "org/jetbrains/kotlin/fir/RelevantTestKlass:testIntCmpCoverage(I)V:IF_ICMPLT1" to BranchBasedCoverage.BranchProbesResults(mapOf("A" to 5, "B" to 5)),
                    "org/jetbrains/kotlin/fir/RelevantTestKlass:testLookUpSwitchCoverage(I)V:LOOKUPSWITCH1" to BranchBasedCoverage.BranchProbesResults(mapOf("1" to 1, "9" to 1, "DFLT" to 8)),
                    "org/jetbrains/kotlin/fir/RelevantTestKlass:testTableSwitchCoverage(I)V:TABLESWITCH1" to BranchBasedCoverage.BranchProbesResults(mapOf("1" to 3, "2" to 3, "DFLT" to 4))
                ))
            ) {
                val o = RelevantTestKlass()
                for (i in 0 until 10) {
                    o.testUnaryRefCmpCoverage(if (i < 5) null else i)
                    o.testBinaryRefCmpCoverage(i)
                    o.testIntCmpCoverage(i)
                    o.testLookUpSwitchCoverage(i)
                    o.testTableSwitchCoverage(i)
                }
            }
        }
    }

    checkIfIgnored {
        val o = UselessTestKlass()
        for (i in 0 until 100) {
            if (i < 75) o.testMethodCoverage1() else o.testMethodCoverage2()
        }
        for (i in 0 until 10) {
            o.testUnaryRefCmpCoverage(if (i < 5) null else i)
            o.testBinaryRefCmpCoverage(i)
            o.testIntCmpCoverage(i)
            o.testLookUpSwitchCoverage(i)
            o.testTableSwitchCoverage(i)
        }
    }
    checkIfIgnored {
        val o = AnotherUselessTestKlass()
        for (i in 0 until 100) {
            if (i < 75) o.testMethodCoverage1() else o.testMethodCoverage2()
        }
        for (i in 0 until 10) {
            o.testUnaryRefCmpCoverage(if (i < 5) null else i)
            o.testBinaryRefCmpCoverage(i)
            o.testIntCmpCoverage(i)
            o.testLookUpSwitchCoverage(i)
            o.testTableSwitchCoverage(i)
        }
    }

    println("All tests for instrumentation Java agent have been passed!")
}

private fun getCoverage(code: () -> Unit): ProgramCoverage {
    CompilerInstrumentation.clearRecords()

    CompilerInstrumentation.shouldProbesBeRecorded = true
    code()
    CompilerInstrumentation.shouldProbesBeRecorded = false

    return ProgramCoverage.createFromProbes()
}

private fun checkRecords(expected: ProgramCoverage, code: () -> Unit) {
    val actual = getCoverage(code)
    assertEquals(expected, actual)
}

private fun checkIfIgnored(code: () -> Unit) {
    assert(getCoverage(code).isEmpty)
}