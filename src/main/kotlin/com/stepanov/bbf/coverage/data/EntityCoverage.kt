package com.stepanov.bbf.coverage.data

import com.stepanov.bbf.coverage.analysis.CoverageType
import kotlinx.serialization.SerialId
import kotlinx.serialization.Serializable

@Serializable
class EntityCoverage() {

    lateinit var name: String

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EntityCoverage

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    var coveredSegments: Int = 0

    var totalSegments: Int = 0

    constructor(
        name: String,
        coveredSegments: Int,
        totalSegments: Int
    ) : this() {
        this.name = name
        this.coveredSegments = coveredSegments
        this.totalSegments = totalSegments
    }

    fun copy(name: String = this.name,
             coveredSegments: Int = this.coveredSegments,
             totalSegments: Int = this.totalSegments): EntityCoverage =
        EntityCoverage(name, coveredSegments, totalSegments)

    override fun toString(): String =
        "$name ($coveredSegments covered / $totalSegments total)"

    fun flatten(): EntityCoverage =
        EntityCoverage(name, if (coveredSegments == 0) 0 else 1, 1)

    fun toEntity(): Entity =
        Entity(name)

    fun toStatistics(coverageType: CoverageType): EntityStatistics =
        EntityStatistics(
            name,
            if (coverageType == CoverageType.FAILED) coveredSegments else 0,
            if (coverageType == CoverageType.PASSED) coveredSegments else 0,
            totalSegments
        )

    private fun binaryOperation(other: EntityCoverage,
                                operation: (Boolean, Boolean) -> Boolean): EntityCoverage {
        require(this == other)
        require(this.totalSegments == 1 && other.totalSegments == 1) { "Entity coverages weren't flattened." }
        val thisCovered = this.coveredSegments != 0
        val otherCovered = other.coveredSegments != 0
        return EntityCoverage(
            name,
            if (operation(thisCovered, otherCovered)) 1 else 0,
            totalSegments
        )
    }

    operator fun plus(other: EntityCoverage): EntityCoverage =
        binaryOperation(other) { x, y -> x || y }

    operator fun minus(other: EntityCoverage): EntityCoverage =
        binaryOperation(other) { x, y -> x && !y }

    infix fun and(other: EntityCoverage): EntityCoverage =
        binaryOperation(other) { x, y -> x && y }

    infix fun xor(other: EntityCoverage): EntityCoverage =
        binaryOperation(other) { x, y -> x xor y }

}