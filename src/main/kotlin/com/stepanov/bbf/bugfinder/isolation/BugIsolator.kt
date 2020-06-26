package com.stepanov.bbf.bugfinder.isolation

import com.stepanov.bbf.bugfinder.executor.WitnessTestsCollector
import com.stepanov.bbf.bugfinder.executor.compilers.JVMCompiler
import com.stepanov.bbf.bugfinder.manager.BugType
import com.stepanov.bbf.bugfinder.mutator.transformations.*
import com.stepanov.bbf.reduktor.parser.PSICreator
import org.jetbrains.kotlin.resolve.BindingContext

object BugIsolator {

    var totalPerformanceTime = 0L
        private set
    var averagePerformanceTime = 0L
        private set
    var numberOfCompilations = 0L
        private set
    var totalIsolationTime = 0L
        private set
    var averageIsolationTime = 0L
        private set
    var numberOfIsolations = 0L
        private set

    fun isolate(path: String, bugType: BugType) {
        var isolationTime = -System.currentTimeMillis()

        val creator = PSICreator("")
        val file = creator.getPSIForFile(path)
        Transformation.file = file
        val collector = WitnessTestsCollector(bugType, listOf(JVMCompiler("-Xnew-inference")))
        Transformation.checker = collector
        mutate(creator.ctx)

        isolationTime += System.currentTimeMillis()
        numberOfIsolations++
        totalIsolationTime += isolationTime
        averageIsolationTime += (isolationTime - averageIsolationTime) / numberOfIsolations

        numberOfCompilations += collector.numberOfCompilations
        totalPerformanceTime += collector.totalPerformanceTime
        averagePerformanceTime += (collector.averagePerformanceTime - averagePerformanceTime) / numberOfIsolations
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