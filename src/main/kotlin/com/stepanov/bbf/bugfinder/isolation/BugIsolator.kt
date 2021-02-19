package com.stepanov.bbf.bugfinder.isolation

import com.stepanov.bbf.bugfinder.executor.CommonCompiler
import com.stepanov.bbf.bugfinder.executor.WitnessTestsCollector
import com.stepanov.bbf.bugfinder.executor.compilers.JVMCompiler
import com.stepanov.bbf.bugfinder.isolation.formulas.*
import com.stepanov.bbf.bugfinder.mutator.transformations.*
import com.stepanov.bbf.reduktor.parser.PSICreator
import org.apache.log4j.Logger
import org.jetbrains.kotlin.resolve.BindingContext
import kotlin.math.sqrt
import kotlin.random.Random

// deprecated lol
object BugIsolator {

    var rankingFormula: RankingFormula = OchiaiRankingFormula

    private val isolationTimes = mutableListOf<Long>()
    var numberOfIsolations = 0L
        private set

    val totalIsolationTime: Long get() = isolationTimes.sum()
    val meanIsolationTime: Long get() = if (numberOfIsolations != 0L) totalIsolationTime / numberOfIsolations else 0L
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
    val meanInstrPerformanceTime: Long get() =
        if (numberOfCompilations != 0L) totalInstrPerformanceTime / numberOfCompilations else 0L

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
    val meanFailingCodeSamples: Long get() =
        if (numberOfIsolations != 0L) totalFailingCodeSamples / numberOfIsolations.toInt() else 0

    val totalPassingCodeSamples: Long get() = successDistributionPerMutation.values.fold(0L) { acc, (_, x) -> acc + x }
    val meanPassingCodeSamples: Long get() =
        if (numberOfIsolations != 0L) totalPassingCodeSamples / numberOfIsolations.toInt() else 0

    var lastNumberOfFailingMutants: Int = 0
        private set
    var lastNumberOfPassingMutants: Int = 0
        private set

    private var allLastFailingMutants = mutableListOf<String>()
    val lastFailingMutants: List<String> get() = allLastFailingMutants.toList()
    private var allLastPassingMutants = mutableListOf<String>()
    val lastPassingMutants: List<String> get() = allLastPassingMutants.toList()

    fun clearStatistics() {
        isolationTimes.clear()
        numberOfIsolations = 0
        instrPerformanceTimes.clear()
        numberOfCompilations = 0
        bugDistributionPerMutation.clear()
        successDistributionPerMutation.clear()
        lastNumberOfFailingMutants = 0
        lastNumberOfFailingMutants = 0
        allLastFailingMutants.clear()
        allLastPassingMutants.clear()
    }

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

        allLastFailingMutants.addAll(collector.bugMutants)
        allLastPassingMutants.addAll(collector.successMutants)
    }

    fun isolate(
        path: String,
        compiler: CommonCompiler = JVMCompiler(),
        formula: RankingFormula = rankingFormula
    ): RankedProgramEntities? {
        val timerStart = -System.currentTimeMillis()

        allLastFailingMutants.clear()
        allLastPassingMutants.clear()

        val creator = PSICreator("")
        val initFile = creator.getPSIForFile(path)
        Transformation.file = initFile

        val collector: WitnessTestsCollector?
        try {
            collector = WitnessTestsCollector(compiler)
        } catch (e: NoBugFoundException) {
            logger.debug(e.message)
            return null
        }
        Transformation.checker = collector


        tailrec fun mutateRecursively(haltChance: Int, maxOrder: Int, order: Int = 1, core: String = initFile.text) {
            collector.clearCurrBugSamples()

            logger.debug("order of mutants: $order")
            logger.debug("current core:\n$core")
            val file = creator.getPSIForText(core)
            Transformation.file = file
            mutate(creator.ctx, collector)

            if (order >= maxOrder || Random.nextInt(1, 100) < haltChance) {
                logger.debug("halting recursion")
                logger.debug("")
            } else {
                mutateRecursively(haltChance, maxOrder, order + 1, collector.currBestFailingMutant)
            }
        }

        mutateRecursively(0, 2)

        val executionStatistics = collector.executionStatistics
        val rankedProgramEntities = RankedProgramEntities.rank(executionStatistics, formula)

        updateStatistics(collector, timerStart + System.currentTimeMillis())

        return rankedProgramEntities
    }

    private fun mutate(context: BindingContext?, collector: WitnessTestsCollector) {
        val mutations = mutableListOf(
                AddBlockToExpression(),
                AddBracketsToExpression(),
                AddDefaultValueToArg(),
                AddNotNullAssertions(),
                AddNullabilityTransformer(),
//                AddPossibleModifiers(),
//                AddReifiedToType(),
//                ChangeArgToAnotherValue(),
//                ChangeConstants(),
//                ChangeModifiers(),
                ChangeOperators(),
                ChangeOperatorsToFunInvocations(),
                ChangeRandomASTNodes(),
                ChangeRandomASTNodesFromAnotherTrees(),
                ChangeRandomLines(),
//                ChangeReturnValueToConstant(),
//                ChangeSmthToExtension(),
//                ChangeVarToNull(),
                RemoveRandomASTNodes(),
                RemoveRandomLines()
        )
//        if (context != null) {
//            mutations += AddSameFunctions(context)
//            mutations += ReinitProperties(context)
//        }
        for (mutation in mutations) {
            collector.clearOverallCounters()
            try {
                executeMutation(mutation)
            } catch (e: ExcessiveMutationException) {
                logger.debug(e.message)
            } catch (e: Throwable) {
                logger.debug(e.message)
            }
        }
    }

    private fun executeMutation(t: Transformation) {
        t.transform()
    }

    private val logger: Logger = Logger.getLogger("isolationTestbedLogger")

}