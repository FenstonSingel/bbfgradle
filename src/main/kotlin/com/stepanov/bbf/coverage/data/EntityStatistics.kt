package com.stepanov.bbf.coverage.data

import com.stepanov.bbf.coverage.analysis.CoverageType
import kotlinx.serialization.Serializable

@Serializable
class EntityStatistics() {

    lateinit var name: String

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EntityStatistics

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    var segmentsRunInFailures: Int = 0

    var segmentsRunInSuccesses: Int = 0

    var segmentCount: Int = 0

    constructor(
        name: String,
        segmentsRunInFailures: Int,
        segmentsRunInSuccesses: Int,
        segmentCount: Int
    ) : this() {
        this.name = name
        this.segmentsRunInFailures = segmentsRunInFailures
        this.segmentsRunInSuccesses = segmentsRunInSuccesses
        this.segmentCount = segmentCount
    }

    fun copy(name: String = this.name,
             segmentsRunInFailures: Int = this.segmentsRunInFailures,
             segmentsRunInSuccesses: Int = this.segmentsRunInSuccesses,
             segmentCount: Int = this.segmentCount): EntityStatistics =
        EntityStatistics(name, segmentsRunInFailures, segmentsRunInSuccesses, segmentCount)

    override fun toString(): String =
        "EntityStatistics of $name"

    fun update(other: Entity, coverageType: CoverageType) {
        require(this.name == other.name)
        require(segmentCount == 1)
        when (coverageType) {
            CoverageType.FAILED -> segmentsRunInFailures++
            CoverageType.PASSED -> segmentsRunInSuccesses++
        }
    }

    fun update(other: EntityCoverage, coverageType: CoverageType) {
        require(this.name == other.name)
        require(segmentCount == other.totalSegments)
        when (coverageType) {
            CoverageType.FAILED -> segmentsRunInFailures += other.coveredSegments
            CoverageType.PASSED -> segmentsRunInSuccesses += other.coveredSegments
        }
    }

}