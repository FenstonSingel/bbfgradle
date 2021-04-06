package com.stepanov.bbf.bugfinder.isolation

import com.stepanov.bbf.coverage.ProgramCoverage

class ExecutionStatistics(val storage: List<Pair<String, EntityExecutionStatistics>>) {

    companion object {
        fun compose(
                origCoverage: ProgramCoverage,
                bugCoverages: Iterable<ProgramCoverage>,
                successCoverages: Iterable<ProgramCoverage>
        ): ExecutionStatistics {
            val result = mutableListOf<Pair<String, EntityExecutionStatistics>>()
            for (entity in ProgramCoverage.entities(origCoverage)) {
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
                result += entity to EntityExecutionStatistics(
                        execsInFails, skipsInFails, execsInSuccesses, skipsInSuccesses
                )
            }
            return ExecutionStatistics(result)
        }

        fun compose(coverages: CoveragesForIsolation): ExecutionStatistics =
                compose(
                    coverages.originalSampleCoverage,
                    coverages.mutantsWithBugCoverages,
                    coverages.mutantsWithoutBugCoverages
                )
    }

}