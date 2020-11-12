package com.stepanov.bbf.coverage

import com.stepanov.bbf.coverage.impl.BranchBasedCoverage
import com.stepanov.bbf.coverage.impl.MethodBasedCoverage
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import kotlin.math.sqrt

interface ProgramCoverage {

    companion object {
        var shouldCoverageBeBinary = true

        fun entities(coverages: Iterable<ProgramCoverage>): List<String> {
            val result = mutableSetOf<String>()
            for (coverage in coverages) {
                result += coverage.entities
            }
            return result.toList()
        }

        fun entities(vararg coverages: ProgramCoverage): List<String> = entities(coverages.toList())

        fun createFromProbes(): ProgramCoverage {
            return when (CompilerInstrumentation.coverageType) {
                CompilerInstrumentation.CoverageType.METHOD -> createFromMethodProbes()
                CompilerInstrumentation.CoverageType.BRANCH -> createFromBranchProbes()
            }
        }

        private fun createFromMethodProbes(): ProgramCoverage {
            return MethodBasedCoverage(CompilerInstrumentation.methodProbes.toMap())
        }

        private fun createFromBranchProbes(): ProgramCoverage {
            val branchProbes = CompilerInstrumentation.branchProbes.toMap()
            val storage = mutableMapOf<String, BranchBasedCoverage.BranchProbesResults>()
            for ((branchName, probeResults) in branchProbes) {
                storage[branchName] = BranchBasedCoverage.BranchProbesResults(probeResults)
            }
            return BranchBasedCoverage(storage)
        }

        private val twoBigDecimal = 2.toBigDecimal()
    }

    // In most cases it is probably more preferable to use ProgramCoverage.entities(),
    // even if there is only one coverage object to use it on.
    val entities: Set<String>

    operator fun get(name: String): Pair<Int, Int>?

    fun copy(): ProgramCoverage

    val isEmpty: Boolean get() = entities().isEmpty()

    fun cosineSimilarity(other: ProgramCoverage): Double =
        calculateCosineSimilarity(other, entities(this, other))

    private fun calculateCosineSimilarity(other: ProgramCoverage, entities: List<String>): Double {
        var dotProduct = BigDecimal.ZERO
        var firstNormSquared = BigDecimal.ZERO
        var secondNormSquared = BigDecimal.ZERO

        for (entity in entities) {
            val firstNumberOfExecutions = this[entity]?.first?.toBigDecimal() ?: BigDecimal.ZERO
            val secondNumberOfExecutions = other[entity]?.first?.toBigDecimal() ?: BigDecimal.ZERO

            dotProduct += firstNumberOfExecutions * secondNumberOfExecutions
            firstNormSquared += firstNumberOfExecutions * firstNumberOfExecutions
            secondNormSquared += secondNumberOfExecutions * secondNumberOfExecutions
        }

        return (dotProduct.setScale(16) / (firstNormSquared.sqrt(16) * secondNormSquared.sqrt(16))).toDouble()
    }

    private fun BigDecimal.sqrt(scale: Int): BigDecimal {
        var x2 = BigDecimal.ZERO
        var x1 = BigDecimal(sqrt(toDouble()))
        while (x2 != x1) {
            x2 = x1
            x1 = divide(x2, scale, RoundingMode.HALF_UP)
            x1 = x1.add(x2)
            x1 = x1.divide(twoBigDecimal, scale, RoundingMode.HALF_UP)
        }
        return x1
    }

}