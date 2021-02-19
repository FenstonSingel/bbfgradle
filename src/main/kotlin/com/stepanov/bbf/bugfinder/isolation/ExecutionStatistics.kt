package com.stepanov.bbf.bugfinder.isolation

import com.stepanov.bbf.coverage.ProgramCoverage

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
                var (execsInFails, skipsInFails) = origCoverage[entity] ?: 0 to 1
                for (bugCoverage in bugCoverages) {
                    val (execs, skips) = bugCoverage[entity] ?: 0 to 1
                    execsInFails += execs
                    skipsInFails += skips
                }
                var execsInSuccesses = 0
                var skipsInSuccesses = 0
                for (successCoverage in successCoverages) {
                    val (execs, skips) = successCoverage[entity] ?: 0 to 1
                    execsInSuccesses += execs
                    skipsInSuccesses += skips
                }
                result[entity] = EntityExecutionStatistics(
                        execsInFails, skipsInFails, execsInSuccesses, skipsInSuccesses
                )
            }
            return ExecutionStatistics(result)
        }

        fun compose(coverages: CoveragesForIsolation): ExecutionStatistics =
                compose(coverages.originalSampleCoverage,
                        coverages.mutantsWithBugCoverages,
                        coverages.mutantsWithoutBugCoverages)
    }

}