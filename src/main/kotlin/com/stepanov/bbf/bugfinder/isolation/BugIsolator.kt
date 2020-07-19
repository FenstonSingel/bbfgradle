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

        // Time performance statistics.

        isolationTime += System.currentTimeMillis()
        numberOfIsolations++
        totalIsolationTime += isolationTime
        meanIsolationTime += (isolationTime - meanIsolationTime) / numberOfIsolations

        numberOfCompilations += collector.numberOfCompilations
        totalInstrPerformanceTime += collector.totalInstrPerformanceTime
        meanInstrPerformanceTime += (collector.meanInstrPerformanceTime - meanInstrPerformanceTime) / numberOfIsolations

        return rankedProgramEntities
    }

    private fun mutate(context: BindingContext?) {
        val mutations = listOf(
            AddBlockToExpression(),
            ChangeRandomLines(),
            ChangeRandomASTNodes(),
            ChangeRandomASTNodesFromAnotherTrees()
        )
        for (mutation in mutations) {
            executeMutation(mutation)
        }
    }

    private fun executeMutation(t: Transformation) {
        Transformation.currentMutation = t.name
        t.transform()
    }

}