package com.stepanov.bbf.coverage

import kotlinx.serialization.Serializable
import kotlin.math.sqrt

@Serializable
class ExecutionCoverage(val storage: Map<String, Int>) : Iterable<Map.Entry<String, Int>> {

    companion object {
        fun createFromRecords(): ExecutionCoverage {
            return ExecutionCoverage(CompilerInstrumentation.entryProbes.toMap())
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ExecutionCoverage

        if (storage != other.storage) return false

        return true
    }

    override fun hashCode(): Int {
        return storage.hashCode()
    }

    fun copy(): ExecutionCoverage {
        return ExecutionCoverage(storage.toMap())
    }

    operator fun get(name: String): Int? = storage[name]

    operator fun contains(name: String): Boolean = this[name] != null

    override fun iterator() = storage.iterator()

    fun uniteEntries(other: ExecutionCoverage): Set<String> {
        val result = mutableSetOf<String>()
        for ((name, _) in this) {
            result += name
        }
        for ((name, _) in other) {
            result += name
        }
        return result
    }

    fun cosineSimilarity(other: ExecutionCoverage): Double {
        var dotProduct = 0.0
        var firstNormSquared = 0.0
        var secondNormSquared = 0.0

        val entries = uniteEntries(other)
        for (entry in entries) {
            val firstNumberOfExecutions = (this[entry] ?: 0).toDouble()
            val secondNumberOfExecutions = (other[entry] ?: 0).toDouble()

            dotProduct += firstNumberOfExecutions * secondNumberOfExecutions
            firstNormSquared += firstNumberOfExecutions * firstNumberOfExecutions
            secondNormSquared += secondNumberOfExecutions * secondNumberOfExecutions
        }

        return dotProduct / (sqrt(firstNormSquared) * sqrt(secondNormSquared))
    }

}