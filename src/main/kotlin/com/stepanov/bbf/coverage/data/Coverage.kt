package com.stepanov.bbf.coverage.data

import kotlinx.serialization.Serializable
import kotlin.math.sqrt

@Serializable
class Coverage : Iterable<EntityCoverage> {

    private val storage: MutableSet<EntityCoverage> = mutableSetOf()

    lateinit var entityType: EntityType

    lateinit var segmentType: SegmentType

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Coverage

        if (entityType != other.entityType) return false
        if (segmentType != other.segmentType) return false
        if (!storage.containsAll(other.storage) || !other.storage.containsAll(storage)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = storage.hashCode()
        result = 31 * result + entityType.hashCode()
        result = 31 * result + segmentType.hashCode()
        return result
    }

    fun copy(): Coverage {
        val copy = Coverage()
        copy.entityType = entityType
        copy.segmentType = segmentType
        copy += storage
        return copy
    }

    operator fun plusAssign(node: EntityCoverage) {
        storage.add(node)
    }

    operator fun plusAssign(nodes: Iterable<EntityCoverage>) {
        for (node in nodes) {
            this += node
        }
    }

    operator fun minusAssign(node: EntityCoverage) {
        storage.remove(node)
    }

    operator fun get(name: String): EntityCoverage? =
        storage.find { it.name == name }

    override fun iterator(): MutableIterator<EntityCoverage> =
        storage.iterator()

    fun removeEmptyEntries() {
        storage.removeAll { it.coveredSegments == 0 }
    }
    
    fun flatten() {
        storage.forEach { it.flatten() }
    }

    operator fun contains(name: String): Boolean =
        this[name] != null

    fun cosineSimilarity(other: Coverage): Double {
        var dotProduct = 0.0
        var firstNorm = 0.0
        var secondNorm = 0.0

        fun updateValues(firstEntity: EntityCoverage?, secondEntity: EntityCoverage?) {
            val reference = when {
                firstEntity != null -> firstEntity
                secondEntity != null -> secondEntity
                else -> throw IllegalArgumentException("At least one of the entities shouldn't be null.")
            }
            val totalSquared = (reference.totalSegments * reference.totalSegments).toDouble()
            if (firstEntity != null) {
                firstNorm += (firstEntity.coveredSegments * firstEntity.coveredSegments) / totalSquared
            }
            if (secondEntity != null) {
                secondNorm += (secondEntity.coveredSegments * secondEntity.coveredSegments) / totalSquared
            }
            if (firstEntity != null && secondEntity != null) {
                dotProduct += (firstEntity.coveredSegments * secondEntity.coveredSegments) / totalSquared
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

    operator fun plus(other: Coverage): Coverage {
        val new = Coverage()
        new.entityType = entityType
        new.segmentType = segmentType
        for (entity in this) {
            val otherEntity = other[entity.name]
            new += if (otherEntity != null) (entity + otherEntity) else entity
        }
        for (otherEntity in other) {
            val entity = this[otherEntity.name]
            if (entity == null) new += otherEntity
        }
        return new
    }

    operator fun plus(coverages: Iterable<Coverage>): Coverage =
        coverages.fold(this) { acc, new -> acc + new }

    operator fun minus(other: Coverage): Coverage {
        val new = Coverage()
        new.entityType = entityType
        new.segmentType = segmentType
        for (entity in this) {
            val otherEntity = other[entity.name]
            new += if (otherEntity != null) (entity - otherEntity) else entity
        }
        return new
    }

    operator fun minus(coverages: Iterable<Coverage>): Coverage =
        coverages.fold(this) { acc, new -> acc - new }

    infix fun and(other: Coverage): Coverage {
        val new = Coverage()
        new.entityType = entityType
        new.segmentType = segmentType
        for (entity in this) {
            val otherEntity = other[entity.name]
            new += if (otherEntity != null) (entity and otherEntity) else entity.copy(coveredSegments = 0)
        }
        return new
    }

    infix fun and(coverages: Iterable<Coverage>): Coverage =
        coverages.fold(this) { acc, new -> acc and new }

    infix fun xor(other: Coverage): Coverage {
        val new = Coverage()
        new.entityType = entityType
        new.segmentType = segmentType
        for (entity in this) {
            val otherEntity = other[entity.name]
            new += if (otherEntity != null) (entity xor otherEntity) else entity
        }
        for (otherEntity in other) {
            val entity = this[otherEntity.name]
            if (entity == null) new += otherEntity
        }
        return new
    }

}