package com.stepanov.bbf.coverage.impl

import com.stepanov.bbf.coverage.ProgramCoverage
import kotlinx.serialization.Serializable

@Serializable
class MethodBasedCoverage(private val methodProbes: Map<String, Int>) : ProgramCoverage {

    override fun entities(): Set<String> = methodProbes.keys

    override fun get(name: String): Pair<Int, Int> {
        val probe = methodProbes[name]
        return if (probe != null) {
            probe to 0
        } else {
            0 to 1
        }
    }

    override fun copy(): ProgramCoverage = MethodBasedCoverage(methodProbes.toMap())

}