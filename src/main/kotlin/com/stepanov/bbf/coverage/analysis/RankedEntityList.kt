package com.stepanov.bbf.coverage.analysis

import kotlin.math.sqrt

class RankedEntityList : Iterable<RankedEntity> {

    private val storage = mutableListOf<RankedEntity>()

    val size: Int
        get() = storage.size

    fun copy(): RankedEntityList {
        val copy = RankedEntityList()
        copy += storage
        return copy
    }

    fun retainFirstElements(until: Int) {
        val iterator = storage.iterator()
        var index = 0
        while (iterator.hasNext()) {
            iterator.next()
            if (index >= until) iterator.remove()
            index++
        }
    }

    operator fun plusAssign(node: RankedEntity) {
        storage.add(node)
    }

    operator fun plusAssign(nodes: Iterable<RankedEntity>) {
        for (node in nodes) {
            this += node
        }
    }

    operator fun minusAssign(node: RankedEntity) {
        storage.remove(node)
    }

    operator fun get(name: String): RankedEntity? =
        storage.find { it.name == name }

    override fun iterator(): Iterator<RankedEntity> =
        storage.iterator()

    fun naiveDistance(other: RankedEntityList): Double {
        var distance = 0.0

        val entities = storage.map { it.name }.toSet() + other.storage.map { it.name }
        for (entity in entities) {
            val thisRank = this[entity]?.rank ?: 0.0
            val thisWeight = if (thisRank == 0.0) 1.0 else thisRank
            val otherRank = other[entity]?.rank ?: 0.0
            val otherWeight = if (otherRank == 0.0) 1.0 else otherRank
            val contribution = thisWeight * otherWeight * sqrt((thisRank - otherRank) * (thisRank - otherRank))
            if (!contribution.isNaN()) distance += contribution
        }

        return distance
    }

    fun cosineSimilarity(other: RankedEntityList): Double {
        var dotProduct = 0.0
        var firstNorm = 0.0
        var secondNorm = 0.0

        fun updateValues(firstEntity: RankedEntity?, secondEntity: RankedEntity?) {
            when {
                firstEntity == null && secondEntity == null -> throw IllegalArgumentException("At least one of the entities shouldn't be null.")
            }
            if (firstEntity != null && !firstEntity.rank.isNaN()) {
                firstNorm += firstEntity.rank * firstEntity.rank
                /*if (secondEntity == null || !firstEntity.rank.isNaN()) {
                    secondNorm += firstEntity.rank * firstEntity.rank
                }*/
            }
            if (secondEntity != null && !secondEntity.rank.isNaN()) {
                secondNorm += secondEntity.rank * secondEntity.rank
                /*if (firstEntity == null || !firstEntity.rank.isNaN()) {
                    firstNorm += secondEntity.rank * secondEntity.rank
                }*/
            }
            if (firstEntity != null && secondEntity != null && !firstEntity.rank.isNaN() && !secondEntity.rank.isNaN()) {
                dotProduct += firstEntity.rank * secondEntity.rank
            }
        }

        for (entity in this) {
            val otherEntity = other[entity.name]
            if (otherEntity == null) updateValues(entity, null) else updateValues(entity, otherEntity)
        }
        for (otherEntity in other) {
            val entity = this[otherEntity.name]
            if (entity == null) updateValues(null, otherEntity)
        }

        return dotProduct / (sqrt(firstNorm) * sqrt(secondNorm))
    }

}