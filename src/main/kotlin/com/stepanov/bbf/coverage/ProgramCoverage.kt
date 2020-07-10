package com.stepanov.bbf.coverage

import kotlin.math.sqrt

interface ProgramCoverage {

    companion object {
        fun entities(vararg coverages: ProgramCoverage): Set<String> {
            val result = mutableSetOf<String>()
            for (coverage in coverages) {
                result += coverage.entities()
            }
            return result
        }

        fun createFromProbes(): ProgramCoverage {
            return if (CompilerInstrumentation.methodProbes.isNotEmpty()) {
                createFromMethodProbes()
            } else {
                createFromBranchProbes()
            }
        }

        private fun createFromMethodProbes(): ProgramCoverage {
            TODO("method-level analogue")
        }

        private fun createFromBranchProbes(): ProgramCoverage {
            val branchProbes = CompilerInstrumentation.branchProbes.toMap()
            val storage = mutableMapOf<String, BranchBasedCoverage.BranchProbesResults>()
            for ((branchName, probeResults) in branchProbes) {
                storage[branchName] = BranchBasedCoverage.BranchProbesResults(probeResults)
            }
            return BranchBasedCoverage(storage)
        }
    }

    fun entities(): Set<String>

    operator fun get(name: String): Pair<Int, Int>

    fun copy(): ProgramCoverage

    fun cosineSimilarity(other: ProgramCoverage): Double {
        var dotProduct = 0.0
        var firstNormSquared = 0.0
        var secondNormSquared = 0.0

        val entities = entities(this, other)
        for (entity in entities) {
            val firstNumberOfExecutions = this[entity].first.toDouble()
            val secondNumberOfExecutions = other[entity].first.toDouble()

            dotProduct += firstNumberOfExecutions * secondNumberOfExecutions
            firstNormSquared += firstNumberOfExecutions * firstNumberOfExecutions
            secondNormSquared += secondNumberOfExecutions * secondNumberOfExecutions

            val firstNumberOfSkips = this[entity].second.toDouble()
            val secondNumberOfSkips = other[entity].second.toDouble()

            dotProduct += firstNumberOfSkips * secondNumberOfSkips
            firstNormSquared += firstNumberOfSkips * firstNumberOfSkips
            secondNormSquared += secondNumberOfSkips * secondNumberOfSkips
        }

        return dotProduct / (sqrt(firstNormSquared) * sqrt(secondNormSquared))
    }

}