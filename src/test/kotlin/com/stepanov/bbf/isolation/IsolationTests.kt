package com.stepanov.bbf.isolation

import com.stepanov.bbf.bugfinder.isolation.EntityExecutionStatistics
import com.stepanov.bbf.bugfinder.isolation.ExecutionStatistics
import com.stepanov.bbf.bugfinder.isolation.RankedProgramEntities
import com.stepanov.bbf.bugfinder.isolation.formulas.Ochiai2RankingFormula
import org.junit.Test
import kotlin.math.abs
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class IsolationTests {

    @Test
    fun factoryTest() {
        val firstStatistics = ExecutionStatistics(
            mapOf(
                "1" to EntityExecutionStatistics(10, 0, 0, 10),
                "2" to EntityExecutionStatistics(5, 5, 5, 5),
                "3" to EntityExecutionStatistics(7, 3, 3, 7)
            )
        )
        val first = RankedProgramEntities.rank(firstStatistics, Ochiai2RankingFormula)
        val secondStatistics = ExecutionStatistics(
            mapOf(
                "1" to EntityExecutionStatistics(10, 0, 0, 10),
                "2" to EntityExecutionStatistics(5, 5, 5, 5),
                "3" to EntityExecutionStatistics(7, 3, 3, 7)
            )
        )
        val second = RankedProgramEntities.rank(secondStatistics, Ochiai2RankingFormula)
        assertEquals(first.storage, second.storage)

        val thirdStatistics = ExecutionStatistics(
            mapOf(
                "1" to EntityExecutionStatistics(10, 0, 0, 10),
                "2" to EntityExecutionStatistics(5, 5, 5, 5),
                "3" to EntityExecutionStatistics(7, 3, 3, 7),
                "4" to EntityExecutionStatistics(0, 10, 10, 0)
            )
        )
        val third = RankedProgramEntities.rank(thirdStatistics, Ochiai2RankingFormula, 3)
        val fourthStatistics = ExecutionStatistics(
            mapOf(
                "1" to EntityExecutionStatistics(10, 0, 0, 10),
                "2" to EntityExecutionStatistics(5, 5, 5, 5),
                "3" to EntityExecutionStatistics(7, 3, 3, 7),
                "4" to EntityExecutionStatistics(3, 7, 7, 3),
                "4" to EntityExecutionStatistics(0, 10, 10, 0)
            )
        )
        val fourth = RankedProgramEntities.rank(fourthStatistics, Ochiai2RankingFormula, 3)
        assertEquals(third.storage, fourth.storage)
    }

    @Test
    fun cosineSimilarityTest() {
        var first = RankedProgramEntities(
                mapOf("one" to 1.0, "two" to 0.5, "three" to 0.25), true
        )
        var second = RankedProgramEntities(
                mapOf("one" to 1.0, "two" to 0.5, "three" to 0.25), true
        )
        assertTrue(abs(first.cosineSimilarity(second) - 1.0) < 10e-12)

        first = RankedProgramEntities(
                mapOf("one" to 1.0, "two" to 0.5, "three" to 0.25), true
        )
        second = RankedProgramEntities(
                mapOf("four" to 1.0, "five" to 0.5, "six" to 0.25), true
        )
        assertTrue(abs(first.cosineSimilarity(second) - 0.0) < 10e-12)

        first = RankedProgramEntities(
                mapOf("one" to 1.0, "two" to 1.0), true
        )
        second = RankedProgramEntities(
                mapOf("one" to 1.0, "three" to 1.0), true
        )
        assertTrue(abs(first.cosineSimilarity(second) - 0.5) < 10e-12)
    }

    @Test
    fun kendallTauDistanceTest() {
        val first = RankedProgramEntities(
            mapOf("1" to 0.0, "2" to 0.25, "3" to 0.5, "4" to 0.75, "5" to 1.0), true
        )
        val second = RankedProgramEntities(
            mapOf("1" to 0.5, "2" to 0.75, "3" to 0.0, "4" to 0.25, "5" to 1.0), true
        )
        assertEquals(first.kendallTauDistance(second), 4)
    }

}