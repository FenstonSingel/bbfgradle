package com.stepanov.bbf.bugfinder.isolation

import com.stepanov.bbf.coverage.ProgramCoverage
import kotlinx.serialization.Serializable

@Serializable
class ExecutionStatistics(val storage: Map<String, EntityExecutionStatistics>) {

    companion object {
        fun compose(
                origCoverage: ProgramCoverage,
                bugCoverages: Iterable<ProgramCoverage>,
                successCoverages: Iterable<ProgramCoverage>
        ): ExecutionStatistics {
            val entities = ProgramCoverage.entities(origCoverage)
            val result = mutableMapOf<String, EntityExecutionStatistics>()
            for (entity in entities) {
                var (execsInFails, skipsInFails) = origCoverage[entity]
                for (bugCoverage in bugCoverages) {
                    val (execs, skips) = bugCoverage[entity]
                    execsInFails += execs
                    skipsInFails += skips
                }
                var execsInSuccesses = 0
                var skipsInSuccesses = 0
                for (successCoverage in successCoverages) {
                    val (execs, skips) = successCoverage[entity]
                    execsInSuccesses += execs
                    skipsInSuccesses += skips
                }
                result[entity] = EntityExecutionStatistics(execsInFails, skipsInFails, execsInSuccesses, skipsInSuccesses)
            }
            return ExecutionStatistics(result)
        }
    }

}