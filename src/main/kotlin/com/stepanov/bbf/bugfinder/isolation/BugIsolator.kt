package com.stepanov.bbf.bugfinder.isolation

import com.stepanov.bbf.bugfinder.executor.WitnessTestsCollector
import com.stepanov.bbf.bugfinder.executor.compilers.JVMCompiler
import com.stepanov.bbf.bugfinder.isolation.formulas.Ochiai2RankingFormula
import com.stepanov.bbf.bugfinder.manager.BugType
import com.stepanov.bbf.bugfinder.mutator.transformations.*
import com.stepanov.bbf.reduktor.parser.PSICreator
import org.jetbrains.kotlin.resolve.BindingContext

object BugIsolator {

    var rankingFormula: RankingFormula = Ochiai2RankingFormula

    var totalInstrPerformanceTime = 0L
        private set
    var meanInstrPerformanceTime = 0L
        private set
    var numberOfCompilations = 0L
        private set
    var totalIsolationTime = 0L
        private set
    var meanIsolationTime = 0L
        private set
    var numberOfIsolations = 0L
        private set

    private val bugDistributionPerMutation = mutableMapOf<String, Long>()
    private val successDistributionPerMutation = mutableMapOf<String, Long>()

    val codeSampleDistributionPerMutation: Map<String, Pair<Double, Double>>
        get() {
            val mutations = (bugDistributionPerMutation.keys + successDistributionPerMutation.keys).toSet()
            val totalBugs = totalFailingCodeSamples
            val totalSuccesses = totalPassingCodeSamples
            val distribution = mutableMapOf<String, Pair<Double, Double>>()
            for (mutation in mutations) {
                val createdBugs = (bugDistributionPerMutation[mutation] ?: 0).toDouble()
                val createdSuccesses = (successDistributionPerMutation[mutation] ?: 0).toDouble()
                distribution[mutation] = createdBugs / totalBugs to createdSuccesses / totalSuccesses
            }
            return distribution
        }

    val totalFailingCodeSamples: Long
        get() = bugDistributionPerMutation.values.fold(0L) { acc, x -> acc + x }
    val totalPassingCodeSamples: Long
        get() = successDistributionPerMutation.values.fold(0L) { acc, x -> acc + x }
    var meanFailingCodeSamples = 0L
        private set
    var meanPassingCodeSamples = 0L
        private set

    fun isolate(path: String, bugType: BugType, formula: RankingFormula = rankingFormula): RankedProgramEntities {
        var isolationTime = -System.currentTimeMillis()

        val creator = PSICreator("")
        val file = creator.getPSIForFile(path)
        Transformation.file = file
        val collector = WitnessTestsCollector(bugType, listOf(JVMCompiler("-Xnew-inference")))
        Transformation.checker = collector
        mutate(creator.ctx)

        val executionStatistics = collector.executionStatistics
        val rankedProgramEntities = RankedProgramEntities.rank(executionStatistics, formula)

        // Performance statistics.

        isolationTime += System.currentTimeMillis()
        numberOfIsolations++
        totalIsolationTime += isolationTime
        meanIsolationTime += (isolationTime - meanIsolationTime) / numberOfIsolations

        numberOfCompilations += collector.numberOfCompilations
        totalInstrPerformanceTime += collector.totalInstrPerformanceTime
        meanInstrPerformanceTime += (collector.meanInstrPerformanceTime - meanInstrPerformanceTime) / numberOfIsolations

        var createdBugs = 0L
        var createdSuccesses = 0L
        collector.bugDistributionPerMutation.forEach { (key, value) ->
            bugDistributionPerMutation.merge(key, value) { old, new -> old + new }
            createdBugs += value
        }
        collector.successDistributionPerMutation.forEach { (key, value) ->
            successDistributionPerMutation.merge(key, value) { old, new -> old + new }
            createdSuccesses += value
        }
        meanFailingCodeSamples += (createdBugs - meanFailingCodeSamples) / numberOfIsolations
        meanPassingCodeSamples += (createdSuccesses - meanPassingCodeSamples) / numberOfIsolations

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