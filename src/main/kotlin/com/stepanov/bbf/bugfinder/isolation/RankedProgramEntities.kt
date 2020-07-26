package com.stepanov.bbf.bugfinder.isolation

import kotlinx.serialization.Serializable
import kotlin.Comparator

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

}