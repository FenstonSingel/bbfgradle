package com.stepanov.bbf.coverage

import com.stepanov.bbf.coverage.impl.CompressedBranchBasedCoverage
import com.stepanov.bbf.coverage.impl.MethodBasedCoverage
import org.junit.Test
import kotlin.math.abs
import kotlin.test.assertTrue

class CoverageTests {

    @Test
    fun cosineSimilarityTest() {
        var first: ProgramCoverage = MethodBasedCoverage(
                mapOf("one" to 1, "two" to 2, "three" to 3)
        )
        var second: ProgramCoverage = MethodBasedCoverage(
                mapOf("one" to 1, "two" to 2, "three" to 3)
        )
        assertTrue(abs(first.cosineSimilarity(second) - 1.0) < 10e-12)

        first = MethodBasedCoverage(
                mapOf("one" to 1, "two" to 2, "three" to 3)
        )
        second = MethodBasedCoverage(
                mapOf("four" to 1, "five" to 2, "six" to 3)
        )
        assertTrue(abs(first.cosineSimilarity(second) - 0.0) < 10e-12)

        first = MethodBasedCoverage(
                mapOf("one" to 2, "two" to 2)
        )
        second = MethodBasedCoverage(
                mapOf("one" to 2, "three" to 2)
        )
        assertTrue(abs(first.cosineSimilarity(second) - 0.5) < 10e-12)

        first = CompressedBranchBasedCoverage(
            setOf(1, 2, 3)
        )
        second = CompressedBranchBasedCoverage(
            setOf(1, 2, 3)
        )
        assertTrue(abs(first.cosineSimilarity(second) - 1.0) < 10e-12)
    }

}