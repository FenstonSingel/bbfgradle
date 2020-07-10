package com.stepanov.bbf.coverage

import kotlinx.serialization.Serializable

// TODO method-level analogue
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
            return execs to total - execs
        }

        operator fun iterator() = results.iterator()
    }

    override fun entities(): Set<String> {
        val result = mutableSetOf<String>()
        for ((branchName, probeResults) in branchProbes) {
            for ((probeName, _) in probeResults) {
                result += "$branchName#$probeName"
            }
        }
        return result
    }

    override fun get(name: String): Pair<Int, Int> {
        val breakPoint = name.indexOf("#")
        val branchName = name.substring(0, breakPoint)
        val probeName = name.substring(breakPoint + 1, name.length)
        val probeResults = branchProbes[branchName] ?: return 0 to 1
        return probeResults[probeName]
    }

    override fun copy(): ProgramCoverage {
        return BranchBasedCoverage(branchProbes.toMap())
    }

}