package com.stepanov.bbf.coverage.impl

import com.stepanov.bbf.coverage.ProgramCoverage
import kotlinx.serialization.Serializable

@Serializable
class BranchBasedCoverage(private val branchProbes: Map<String, BranchProbesResults>) : ProgramCoverage {

    @Serializable
    data class BranchProbesResults(val results: Map<String, Int>) {
        private val total: Int
        init {
            var temp = 0
            for ((_, execs) in results) {
                temp += execs
            }
            total = temp
        }

        operator fun get(name: String): Pair<Int, Int> {
            val execs = results[name] ?: 0
            if (ProgramCoverage.shouldCoverageBeBinary) {
                return if (execs != 0) 1 to 0 else 0 to 1
            }
            return execs to total - execs
        }

        operator fun iterator() = results.iterator()
    }

    override val entities: Set<String>
        get() {
            val result = mutableSetOf<String>()
            for ((branchName, probeResults) in branchProbes) {
                for ((probeName, _) in probeResults) {
                    result += "$branchName#$probeName"
                }
            }
            return result
        }

    override fun get(name: String): Pair<Int, Int>? {
        val breakPoint = name.indexOf("#")
        val branchName = name.substring(0, breakPoint)
        val probeName = name.substring(breakPoint + 1, name.length)
        val probeResults = branchProbes[branchName] ?: return null
        return probeResults[probeName]
    }

    override fun copy(): ProgramCoverage = BranchBasedCoverage(branchProbes.toMap())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BranchBasedCoverage

        if (branchProbes != other.branchProbes) return false

        return true
    }

    override fun hashCode(): Int {
        return branchProbes.hashCode()
    }

}