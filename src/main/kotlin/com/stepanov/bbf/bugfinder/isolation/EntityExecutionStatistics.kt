package com.stepanov.bbf.bugfinder.isolation

import kotlinx.serialization.Serializable

@Serializable
data class EntityExecutionStatistics(
        val execsInFails: Int,
        val skipsInFails: Int,
        val execsInSuccesses: Int,
        val skipsInSuccesses: Int
)