package com.stepanov.bbf.coverage

import kotlinx.serialization.Serializable

@Serializable
class ExecutionCoverage(val storage: Map<String, Int>) {

    companion object {
        fun createFromRecords(): ExecutionCoverage {
            return ExecutionCoverage(CompilerInstrumentation.probes.toMap())
        }
    }

    fun copy(): ExecutionCoverage {
        return ExecutionCoverage(storage.toMap())
    }

}