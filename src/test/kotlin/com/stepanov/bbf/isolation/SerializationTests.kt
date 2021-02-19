package com.stepanov.bbf.isolation

import com.stepanov.bbf.bugfinder.isolation.CoveragesForIsolation
import com.stepanov.bbf.bugfinder.isolation.MutantsForIsolation
import com.stepanov.bbf.coverage.impl.BranchBasedCoverage
import com.stepanov.bbf.coverage.impl.MethodBasedCoverage
import org.junit.Test
import kotlin.test.assertEquals

class SerializationTests {
    @Test
    fun mutantsTest() {
        val mutants = MutantsForIsolation(
            "originalSample.kt", "default",
                "originalSample", listOf("mutantA, mutantB, mutantC")
        )
        mutants.export("tmp/trash/mutants")
        assertEquals(mutants, MutantsForIsolation.import("tmp/trash/mutants"))
    }

    @Test
    fun coveragesTest() {
        val originalCoverage = MethodBasedCoverage(mapOf())
        val coverageForMutantWithBug = MethodBasedCoverage(mapOf("one" to 1, "two" to 2, "three" to 3))
        val coverageForMutantWithoutBug = BranchBasedCoverage(mapOf(
                "one" to BranchBasedCoverage.BranchProbesResults(mapOf("A" to 5, "B" to 5)),
                "two" to BranchBasedCoverage.BranchProbesResults(mapOf("A" to 5, "B" to 5))
        ))
        val coverages = CoveragesForIsolation(
                "originalSample.kt", "default-default",
                originalCoverage, listOf(coverageForMutantWithBug), listOf(coverageForMutantWithoutBug)
        )
        coverages.export("tmp/trash/coverages")
        assertEquals(coverages, CoveragesForIsolation.import("tmp/trash/coverages"))
    }
}