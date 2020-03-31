package com.stepanov.bbf.bugfinder.isolation

import com.stepanov.bbf.bugfinder.executor.WitnessTestsCollector
import com.stepanov.bbf.bugfinder.executor.compilers.JVMCompiler
import com.stepanov.bbf.bugfinder.manager.BugType
import com.stepanov.bbf.bugfinder.mutator.transformations.*
import com.stepanov.bbf.reduktor.parser.PSICreator
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import kotlin.random.Random

object BugIsolator {

    fun isolate(path: String, bugType: BugType): Pair<MutantCoverages, List<MutationStatistics>> {
        val creator = PSICreator("")
        val file = creator.getPSIForFile(path)
        Transformation.file = file
        val collector = WitnessTestsCollector(bugType, listOf(JVMCompiler("-Xnew-inference")))
        Transformation.checker = collector
        mutate(creator.ctx)
        return collector.mutantCoverages to collector.mutationStatistics.map { (_, obj) -> obj }
    }

    private fun mutate(context: BindingContext?) {
        val mutations = listOf(
            AddBlockToExpression(),
            ChangeRandomLines()
        )
        for (mutation in mutations) {
            executeMutation(mutation)
        }
        executeMutation(ChangeRandomASTNodes())
        executeMutation(ChangeRandomASTNodesFromAnotherTrees())
    }

    private fun executeMutation(t: Transformation) {
        Transformation.currentMutation = t.name
        t.transform()
    }

}