package com.stepanov.bbf.bugfinder.executor

import com.stepanov.bbf.bugfinder.isolation.ExecutionStatistics
import com.stepanov.bbf.bugfinder.manager.BugType
import com.stepanov.bbf.bugfinder.mutator.transformations.Transformation.Companion.file
import com.stepanov.bbf.bugfinder.util.BoundedSortedByModelElementSet
import com.stepanov.bbf.coverage.CompilerInstrumentation
import com.stepanov.bbf.coverage.ProgramCoverage
import com.stepanov.bbf.reduktor.parser.PSICreator
import org.jetbrains.kotlin.psi.KtFile

class WitnessTestsCollector(
    bugType: BugType,
    compilers: List<CommonCompiler>
) : Checker() {

    // list of things to note when porting the code to a refactored version
    // TODO Compilation timeouts (deleted in this branch) are a potential problem in the future.
    // TODO Check comments in MultiCompilerCrashCollector too.

    val checker = when (bugType) {
        BugType.BACKEND -> MultiCompilerCrashChecker(compilers.first())
        BugType.DIFFBEHAVIOR -> DiffBehaviorChecker(compilers)
        BugType.DIFFCOMPILE -> DiffCompileChecker(compilers)
        BugType.FRONTEND -> MultiCompilerCrashChecker(compilers.first())
        BugType.UNKNOWN -> MultiCompilerCrashChecker(compilers.first())
    }

    init {
        checker.pathToFile = file.name
    }

    var totalPerformanceTime = 0L
        private set
    var averagePerformanceTime = 0L
        private set
    var numberOfCompilations = 0L
        private set

    // TODO Statistics on how useful every mutation is.

    private fun compile(text: String): Pair<Boolean, ProgramCoverage?> {
        val status = checker.checkTest(text)
        var coverage: ProgramCoverage? = null
        if (!CompilerInstrumentation.isEmpty) {
            coverage = ProgramCoverage.createFromProbes()
        }
        return status to coverage
    }

    private val originalCoverage: ProgramCoverage
    init {
        val (status, coverage) = compile(file.text)
        if (!status || coverage == null) throw IllegalArgumentException("A project should contain a bug in order to isolate it.")
        originalCoverage = coverage
    }

        private var tempCosineDistance: Double = 0.0

    override fun checkCompiling(file: KtFile): Boolean {
        val (status, coverage) = compile(file.text)
        if (coverage != null) {
            numberOfCompilations++
            val performanceTime = CompilerInstrumentation.performanceTimer
            totalPerformanceTime += performanceTime
            averagePerformanceTime += (performanceTime - averagePerformanceTime) / numberOfCompilations

            tempCosineDistance = 1 - originalCoverage.cosineSimilarity(coverage)
            if (status) {
                bugDatabase.add(coverage.copy())
            } else {
                successDatabase.add(coverage.copy())
            }
        }
        return false // This is fine, no bugs.
    }

    override fun checkTextCompiling(text: String): Boolean =
            checkCompiling(PSICreator("").getPSIForText(text, false))

    private val bugDatabase = BoundedSortedByModelElementSet(
        originalCoverage.copy(),
        100,
        Comparator { _, _ -> (tempCosineDistance * 10E7).toInt() },
        isSortingReversed = true
    )

    private val successDatabase = BoundedSortedByModelElementSet(
        originalCoverage.copy(),
        100,
        Comparator { _, _ -> (tempCosineDistance * 10E7).toInt() },
        isSortingReversed = false
    )

    val executionStatistics: ExecutionStatistics
        get() = ExecutionStatistics.compose(originalCoverage, bugDatabase.toList(), successDatabase.toList())

}