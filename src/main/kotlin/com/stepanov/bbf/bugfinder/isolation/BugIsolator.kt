package com.stepanov.bbf.bugfinder.isolation

import com.stepanov.bbf.bugfinder.executor.WitnessTestsCollector
import com.stepanov.bbf.bugfinder.executor.compilers.JVMCompiler
import com.stepanov.bbf.bugfinder.isolation.formulas.Ochiai2RankingFormula
import com.stepanov.bbf.bugfinder.manager.BugType
import com.stepanov.bbf.bugfinder.mutator.transformations.*
import com.stepanov.bbf.reduktor.parser.PSICreator
import org.jetbrains.kotlin.resolve.BindingContext
import kotlin.math.sqrt

object BugIsolator {

    var rankingFormula: RankingFormula = Ochiai2RankingFormula

    private val isolationTimes = mutableListOf<Long>()
    var numberOfIsolations = 0L
        private set

    val totalIsolationTime: Long get() = isolationTimes.sum()
    val meanIsolationTime: Long get() = totalIsolationTime / numberOfIsolations
    val isolationTimeSD: Long
        get() {
            val mean = meanIsolationTime
            val sum = isolationTimes.fold(0L) { acc, l -> acc + (l - mean) * (l - mean) }.toDouble()
            return sqrt(sum / if (numberOfIsolations == 1L) numberOfIsolations else numberOfIsolations - 1).toLong()
        }
    val minIsolationTime: Long get() = isolationTimes.min() ?: 0
    val maxIsolationTime: Long get() = isolationTimes.max() ?: 0

    private val instrPerformanceTimes = mutableListOf<Long>()
    var numberOfCompilations = 0L
        private set

    val totalInstrPerformanceTime: Long get() = instrPerformanceTimes.sum()
    val meanInstrPerformanceTime: Long get() = totalInstrPerformanceTime / numberOfCompilations

    private val bugDistributionPerMutation = mutableMapOf<String, Pair<Long, Long>>()
    private val successDistributionPerMutation = mutableMapOf<String, Pair<Long, Long>>()

    val codeSampleQualityPerMutation: Map<String, Pair<Long, Long>>
        get() {
            val mutations = (bugDistributionPerMutation.keys + successDistributionPerMutation.keys).toSet()
            val distribution = mutableMapOf<String, Pair<Long, Long>>()
            for (mutation in mutations) {
                val (cosDistSumF, numOfFails) = bugDistributionPerMutation[mutation] ?: -1L to 1L
                val (cosDistSumP, numOfPasses) = successDistributionPerMutation[mutation] ?: -1L to 1L
                distribution[mutation] = cosDistSumF / numOfFails to cosDistSumP / numOfPasses
            }
            return distribution
        }

    val codeSampleDistributionPerMutation: Map<String, Pair<Double, Double>>
        get() {
            val mutations = (bugDistributionPerMutation.keys + successDistributionPerMutation.keys).toSet()
            val totalBugs = totalFailingCodeSamples
            val totalSuccesses = totalPassingCodeSamples
            val distribution = mutableMapOf<String, Pair<Double, Double>>()
            for (mutation in mutations) {
                val createdBugs = (bugDistributionPerMutation[mutation]?.second ?: 0).toDouble()
                val createdSuccesses = (successDistributionPerMutation[mutation]?.second ?: 0).toDouble()
                distribution[mutation] = createdBugs / totalBugs to createdSuccesses / totalSuccesses
            }
            return distribution
        }

    val totalFailingCodeSamples: Long get() = bugDistributionPerMutation.values.fold(0L) { acc, (_, x) -> acc + x }
    val meanFailingCodeSamples: Long get() = totalFailingCodeSamples / numberOfIsolations.toInt()

    val totalPassingCodeSamples: Long get() = successDistributionPerMutation.values.fold(0L) { acc, (_, x) -> acc + x }
    val meanPassingCodeSamples: Long get() = totalPassingCodeSamples / numberOfIsolations.toInt()

    var lastNumberOfFailingMutants: Int = 0
        private set
    var lastNumberOfPassingMutants: Int = 0
        private set

    private fun updateStatistics(collector: WitnessTestsCollector, time: Long) {
        isolationTimes += time
        numberOfIsolations++

        instrPerformanceTimes += collector.instrPerformanceTimes
        numberOfCompilations += collector.numberOfCompilations

        collector.bugDistributionPerMutation.forEach { (key, value) ->
            bugDistributionPerMutation.merge(key, value) {
                (oldSum, oldNum), (newSum, newNum) -> oldSum + newSum to oldNum + newNum
            }
        }
        collector.successDistributionPerMutation.forEach { (key, value) ->
            successDistributionPerMutation.merge(key, value) {
                (oldSum, oldNum), (newSum, newNum) -> oldSum + newSum to oldNum + newNum
            }
        }

        lastNumberOfFailingMutants = collector.numberOfBugs
        lastNumberOfPassingMutants = collector.numberOfSuccesses
    }


    fun isolate(path: String, bugType: BugType, formula: RankingFormula = rankingFormula): RankedProgramEntities {
        val timerStart = -System.currentTimeMillis()

        val creator = PSICreator("")
        val file = creator.getPSIForFile(path)
        Transformation.file = file
        val collector = WitnessTestsCollector(bugType, listOf(JVMCompiler("-Xnew-inference")))
        Transformation.checker = collector
        mutate(creator.ctx)

        val executionStatistics = collector.executionStatistics
        val rankedProgramEntities = RankedProgramEntities.rank(executionStatistics, formula)

        updateStatistics(collector, timerStart + System.currentTimeMillis())

        return rankedProgramEntities
    }

    private fun mutate(context: BindingContext?) {
        val mutations = mutableListOf(
                AddBlockToExpression(),
                AddBracketsToExpression(),
                AddDefaultValueToArg(),
                AddNotNullAssertions(),
                AddNullabilityTransformer(),
                AddPossibleModifiers(),
                AddReifiedToType(),
                ChangeArgToAnotherValue(),
                ChangeConstants(),
                ChangeModifiers(),
                ChangeOperators(),
                ChangeOperatorsToFunInvocations(),
                ChangeRandomASTNodes(),
                ChangeRandomASTNodesFromAnotherTrees(),
                ChangeRandomLines(),
                ChangeReturnValueToConstant(),
                ChangeSmthToExtension(),
                ChangeVarToNull(),
                RemoveRandomLines()
        )
        if (context != null) {
            mutations += AddSameFunctions(context)
            mutations += ReinitProperties(context)
        }
        for (mutation in mutations) {
            executeMutation(mutation)
        }
    }

    private fun executeMutation(t: Transformation) {
        Transformation.currentMutation = t.name
        t.transform()
    }

}