package com.stepanov.bbf.bugfinder.isolation

data class EntityExecutionData(
        val execsInFails: Int,
        val skipsInFails: Int,
        val execsInSuccesses: Int,
        val skipsInSuccesses: Int
)