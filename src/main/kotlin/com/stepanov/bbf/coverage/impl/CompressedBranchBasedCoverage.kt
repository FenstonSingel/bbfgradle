package com.stepanov.bbf.coverage.impl

import com.stepanov.bbf.coverage.ProgramCoverage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("compressedBranchBasedCoverage")
class CompressedBranchBasedCoverage(private val binaryBranchProbeHashes: Set<Int>) : ProgramCoverage {

    override val entities: Set<String> get() = binaryBranchProbeHashes.map { it.toString() }.toSet()

    override fun get(name: String): Pair<Int, Int>? {
        val nameHash = name.toIntOrNull() ?: name.hashCode()
        return if (nameHash in binaryBranchProbeHashes) 1 to 0 else null
    }

    override fun copy(): ProgramCoverage = CompressedBranchBasedCoverage(binaryBranchProbeHashes.toSet())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CompressedBranchBasedCoverage

        if (binaryBranchProbeHashes != other.binaryBranchProbeHashes) return false

        return true
    }

    override fun hashCode(): Int {
        return binaryBranchProbeHashes.hashCode()
    }

}