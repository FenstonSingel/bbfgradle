package com.stepanov.bbf.bugfinder.isolation

import kotlinx.serialization.Serializable
import java.util.*

@Serializable
class RankedProgramEntities(val storage: SortedSet<Pair<String, Double>>) {

    companion object {
        fun rank(statistics: ExecutionStatistics, formula: RankingFormula): RankedProgramEntities {
            val result = statistics.storage
                    .map { (entity, statistics) -> entity to formula.calculate(statistics) }
                    .toSortedSet(
                            Comparator { a, b -> compare(a, b, formula.isRankDescending) }
                    )
            return RankedProgramEntities(result)
        }

        private fun compare(pair1: Pair<String, Double>, pair2: Pair<String, Double>, isRankDescending: Boolean): Int {
            val (entity1, rank1) = pair1
            val (entity2, rank2) = pair2
            val rankComparison = rank1.compareTo(rank2) * if (isRankDescending) -1 else 1
            return if (rankComparison != 0) rankComparison else entity1.compareTo(entity2)
        }
    }

}