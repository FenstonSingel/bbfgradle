package com.stepanov.bbf.bugfinder.isolation

import com.stepanov.bbf.coverage.ProgramCoverage
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.io.File
import kotlin.Comparator
import kotlin.math.roundToInt
import kotlin.math.sign
import kotlin.math.sqrt

@Serializable
class RankedProgramEntities(val storage: Map<String, Double>, private val isRankDescending: Boolean) {

    companion object {
        fun rank(
            origCoverage: ProgramCoverage,
            bugCoverages: Iterable<ProgramCoverage>,
            successCoverages: Iterable<ProgramCoverage>,
            formula: RankingFormula
        ): RankedProgramEntities {
            val executionData = mutableListOf<Pair<String, EntityExecutionData>>()
            for (entity in ProgramCoverage.entities(origCoverage)) {
                var (execsInFails, skipsInFails) = origCoverage[entity] ?: 0 to 1
                for (bugCoverage in bugCoverages) {
                    val (execs, skips) = bugCoverage[entity] ?: 0 to 1
                    execsInFails += execs
                    skipsInFails += skips
                }
                var execsInSuccesses = 0
                var skipsInSuccesses = 0
                for (successCoverage in successCoverages) {
                    val (execs, skips) = successCoverage[entity] ?: 0 to 1
                    execsInSuccesses += execs
                    skipsInSuccesses += skips
                }
                executionData += entity to EntityExecutionData(
                    execsInFails, skipsInFails, execsInSuccesses, skipsInSuccesses
                )
            }

            val result = executionData.map { (entity, statistics) -> entity to formula(statistics) }
            return RankedProgramEntities(result.normalize().associate { it }, formula.isRankDescending)
        }

        fun rank(
            coverages: CoveragesForIsolation,
            rankingFormula: RankingFormula
        ): RankedProgramEntities = rank(
            coverages.originalSampleCoverage,
            coverages.mutantsWithBugCoverages,
            coverages.mutantsWithoutBugCoverages,
            rankingFormula
        )

        fun import(filePath: String): RankedProgramEntities {
            return json.parse(serializer(), File(filePath).readText())
        }

        private fun List<Pair<String, Double>>.normalize(): List<Pair<String, Double>> {
            val (_, minRank) = minBy { (_, rank) -> rank } ?: "" to 0.0
            val (_, maxRank) = maxBy { (_, rank) -> rank } ?: "" to 1.0
            val rangeLength = maxRank - minRank
            return map { (entity, rank) -> entity to if (rangeLength != 0.0) (rank - minRank) / rangeLength else 0.5 }
        }

        private fun compare(pair1: Pair<String, Double>, pair2: Pair<String, Double>, isRankDescending: Boolean): Int {
            val (entity1, rank1) = pair1
            val (entity2, rank2) = pair2
            val rankComparison = rank1.compareTo(rank2) * if (isRankDescending) -1 else 1
            return if (rankComparison != 0) rankComparison else entity1.compareTo(entity2)
        }

        private val json = Json(JsonConfiguration.Stable)
    }

    fun export(filePath: String) {
        File(filePath).writeText(json.stringify(serializer(), this))
    }

    fun topFraction(fraction: Double): RankedProgramEntities {
        return RankedProgramEntities(
            toList()
                .dropLast((storage.size * (1.0 - fraction)).roundToInt())
                .normalize().associate { it },
            isRankDescending
        )
    }

    fun toList(): List<Pair<String, Double>> = storage
            .map { (entity, rank) -> entity to rank }
            .sortedWith( Comparator { a, b -> compare(a, b, isRankDescending) } )

    fun cosineSimilarity(other: RankedProgramEntities): Double {
        var dotProduct = 0.0
        var firstNormSquared = 0.0
        var secondNormSquared = 0.0

        val entities = (this.storage.keys.toSet() + other.storage.keys.toSet()).toList()
        for (entity in entities) {
            val first = this.storage[entity] ?: 0.0
            val second = other.storage[entity] ?: 0.0

            dotProduct += first * second
            firstNormSquared += first * first
            secondNormSquared += second * second
        }

        return dotProduct / (sqrt(firstNormSquared) * sqrt(secondNormSquared))
    }

    // Seems like not a very good idea after all.
    fun kendallTauDistance(other: RankedProgramEntities): Int {
        var discordantPairs = 0
        var deltaOfTies = 0

        val entities = (this.storage.keys.toSet() + other.storage.keys.toSet()).toList()
        for (i in entities.indices) {
            val firstEntity = entities[i]
            for (j in (i + 1) until entities.size) {
                val secondEntity = entities[j]

                val firstRankInThis = this.storage[firstEntity] ?: 0.0
                val secondRankInThis = this.storage[secondEntity] ?: 0.0
                val firstRankInOther = other.storage[firstEntity] ?: 0.0
                val secondRankInOther = other.storage[secondEntity] ?: 0.0

                if (sign(secondRankInThis - firstRankInThis) != sign(secondRankInOther - firstRankInOther)) {
                    discordantPairs++
                } else {
                    val isTiedInThis = firstRankInThis == secondRankInThis
                    val isTiedInOther = firstRankInOther == secondRankInOther
                    if (isTiedInThis xor isTiedInOther) {
                        deltaOfTies++
                    }
                }
            }
        }

        return discordantPairs
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RankedProgramEntities

        if (storage != other.storage) return false

        return true
    }

    override fun hashCode(): Int {
        return storage.hashCode()
    }

}