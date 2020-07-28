package com.stepanov.bbf.bugfinder.isolation

import kotlinx.serialization.Serializable
import kotlin.Comparator
import kotlin.math.sqrt

@Serializable
class RankedProgramEntities(val storage: Map<String, Double>, private val isRankDescending: Boolean) {

    companion object {
        fun rank(statistics: ExecutionStatistics, formula: RankingFormula): RankedProgramEntities {
            val result = statistics.storage.mapValues { (_, statistics) -> formula(statistics) }
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

    // TODO Write tests for this function.
    // TODO Write a function calculating Kendall's tau function.

}