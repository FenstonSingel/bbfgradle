package com.stepanov.bbf.coverage.data

import com.stepanov.bbf.bugfinder.isolation.MutantCoverages
import com.stepanov.bbf.coverage.analysis.CoverageType
import com.stepanov.bbf.coverage.analysis.RankedEntity
import com.stepanov.bbf.coverage.analysis.RankedEntityList
import com.stepanov.bbf.coverage.analysis.RankingFormula

class EntityStatisticsSet() : Iterable<EntityStatistics> {

    private val storage: MutableMap<String, EntityStatistics> = mutableMapOf()

    private var failuresCount = 0

    val failures: Int
        get() = failuresCount

    private var successesCount = 0

    val successes: Int
        get() = successesCount

    fun copy(): EntityStatisticsSet {
        val copy = EntityStatisticsSet()
        copy.storage.putAll(storage)
        return copy
    }

    operator fun get(name: String): EntityStatistics? =
        storage[name]

    override fun iterator(): MutableIterator<EntityStatistics> =
        object : MutableIterator<EntityStatistics> {
            private val innerIterator = storage.iterator()

            override fun hasNext(): Boolean =
                innerIterator.hasNext()

            override fun next(): EntityStatistics =
                innerIterator.next().value

            override fun remove() {
                innerIterator.remove()
            }
        }

    // TODO check how this works and fix in case of necessity
    fun removeIrrelevantEntries() {
        val iter = storage.iterator()
        while (iter.hasNext()) {
            val entityEntry = iter.next()
            val entity = entityEntry.value
            if (entity.segmentsRunInFailures == 0 && entity.segmentsRunInSuccesses / entity.segmentCount == successesCount) {
                iter.remove()
            }
        }
    }

    constructor(
        coverages: MutantCoverages,
        noise: Coverage? = null
    ) : this() {
        failuresCount++
        for (entity in coverages.original) {
            if (noise != null && entity in noise) continue
            storage[entity.name] = entity.toStatistics(CoverageType.FAILED)
        }
        for (coverage in coverages.failureCoverages) {
            failuresCount++
            for (entity in coverage) {
                this[entity.name]?.update(entity, CoverageType.FAILED)
            }
        }
        for (coverage in coverages.successCoverages) {
            successesCount++
            for (entity in coverage) {
                this[entity.name]?.update(entity, CoverageType.PASSED)
            }
        }
    }

    fun rank(rankingFormula: RankingFormula): RankedEntityList {
        val result = RankedEntityList()
        result += storage
            .map {
                val entity = it.value
                val doubleSegmentCount = entity.segmentCount.toDouble()
                val rank = rankingFormula.calculate(
                    entity.segmentsRunInFailures / doubleSegmentCount,
                    entity.segmentsRunInSuccesses / doubleSegmentCount,
                    (failuresCount * entity.segmentCount - entity.segmentsRunInFailures) / doubleSegmentCount,
                    (successesCount * entity.segmentCount - entity.segmentsRunInSuccesses) / doubleSegmentCount
                )
                RankedEntity(entity.name, rank)
            }
            .sortedByDescending { it.rank } // TODO there are problems with lists ranked by O metric
        return result
    }

}