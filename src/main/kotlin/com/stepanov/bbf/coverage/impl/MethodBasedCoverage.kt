package com.stepanov.bbf.coverage.impl

import com.stepanov.bbf.coverage.ProgramCoverage
import kotlinx.serialization.Serializable

@Serializable
class MethodBasedCoverage(private val methodProbes: Map<String, Int>) : ProgramCoverage {

    override val entities: Set<String> get() = methodProbes.keys

    override fun get(name: String): Pair<Int, Int>? {
        val probe = methodProbes[name]
        return probe?.let { (if (ProgramCoverage.shouldCoverageBeBinary) 1 else probe) to 0 }
    }

    override fun copy(): ProgramCoverage = MethodBasedCoverage(methodProbes.toMap())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MethodBasedCoverage

        if (methodProbes != other.methodProbes) return false

        return true
    }

    override fun hashCode(): Int {
        return methodProbes.hashCode()
    }

}