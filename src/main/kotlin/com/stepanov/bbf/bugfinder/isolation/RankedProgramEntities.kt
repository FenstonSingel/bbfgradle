package com.stepanov.bbf.bugfinder.isolation

import kotlinx.serialization.Serializable
import kotlin.Comparator
import kotlin.math.sign
import kotlin.math.sqrt

@Serializable
class RankedProgramEntities(val storage: Map<String, Double>, private val isRankDescending: Boolean) {

    companion object {
        fun rank(
            statistics: ExecutionStatistics,
            formula: RankingFormula,
            numberOfEntities: Int? = null
        ): RankedProgramEntities {
            val rawResult = statistics.storage.mapValues { (_, statistics) -> formula(statistics) }
            val result = if (numberOfEntities != null) {
                val sortedResult = rawResult.toSortedMap()
                val iterator = sortedResult.iterator()
                val temp = mutableMapOf<String, Double>()
                for (i in 0 until numberOfEntities) {
                    val (entity, rank) = if (iterator.hasNext()) iterator.next() else break
                    temp[entity] = rank
                }
                temp
            } else {
                rawResult
            }
            return RankedProgramEntities(result, formula.isRankDescending)
        }
    }

    private fun compare(pair1: Pair<String, Double>, pair2: Pair<String, Double>): Int {
        val (entity1, rank1) = pair1
        val (entity2, rank2) = pair2
        val rankComparison = rank1.compareTo(rank2) * if (isRankDescending) -1 else 1
        return if (rankComparison != 0) rankComparison else entity1.compareTo(entity2)
    }

    fun toList(): List<Pair<String, Double>> {
        return storage
            .map { (entity, rank) -> entity to rank }
            .sortedWith( Comparator { a, b -> compare(a, b) } )
    }

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

    // TODO If this is a good idea, fix the O(n^2) complexity.
    // TODO Write tests for this function.
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

}