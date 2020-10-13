package com.stepanov.bbf.bugfinder.executor

import com.stepanov.bbf.bugfinder.isolation.ExcessiveMutationException
import com.stepanov.bbf.bugfinder.isolation.ExecutionStatistics
import com.stepanov.bbf.bugfinder.isolation.NoBugFoundException
import com.stepanov.bbf.bugfinder.manager.BugType
import com.stepanov.bbf.bugfinder.mutator.transformations.Transformation
import com.stepanov.bbf.bugfinder.mutator.transformations.Transformation.Companion.file
import com.stepanov.bbf.bugfinder.util.BoundedSortedByModelElementSet
import com.stepanov.bbf.coverage.CompilerInstrumentation
import com.stepanov.bbf.coverage.ProgramCoverage
import com.stepanov.bbf.reduktor.parser.PSICreator
import org.apache.log4j.Logger
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

    private val _instrPerformanceTimes = mutableListOf<Long>()
    val instrPerformanceTimes: List<Long> get() = _instrPerformanceTimes.toList()
    var numberOfCompilations = 0L
        private set

    val bugDistributionPerMutation = mutableMapOf<String, Pair<Long, Long>>()
    val successDistributionPerMutation = mutableMapOf<String, Pair<Long, Long>>()

    private fun compile(text: String): Pair<Boolean, ProgramCoverage?> {
        val status = checker.checkTest(text, "tmp/tmp.kt")
        var coverage: ProgramCoverage? = null
        if (!CompilerInstrumentation.isEmpty) {
            coverage = ProgramCoverage.createFromProbes()
        }
        return status to coverage
    }

    private val originalCoverage: ProgramCoverage
    init {
        val (status, coverage) = compile(file.text)
        if (!status || coverage == null) throw NoBugFoundException("A project should contain a bug in order to isolate it.")
        originalCoverage = coverage
    }

    private var tempCosineDistance: Double = 0.0

    override fun checkCompiling(file: KtFile): Boolean {
        val (status, coverage) = compile(file.text)
        if (coverage != null) {
            // Time performance statistics.
            _instrPerformanceTimes += CompilerInstrumentation.instrumentationPerformanceTime
            numberOfCompilations++

            tempCosineDistance = 1 - originalCoverage.cosineSimilarity(coverage)

            // Mutation usefulness statistics.
            (if (status) bugDistributionPerMutation else successDistributionPerMutation)
                    .merge(Transformation.currentMutation, (tempCosineDistance * 10E7).toLong() to 1L) {
                        (prevSum, prevNum), (newCD, one) -> prevSum + newCD to prevNum + one
                    }

            overallMutants++
            logger.debug("Mutants by ${Transformation.currentMutation}: $overallMutants")
            if (status) {
                bugDatabase.add(coverage.copy())
            } else {
                successDatabase.add(coverage.copy())
            }

            if (overallMutants >= maxMutationIterations) {
                throw ExcessiveMutationException("Mutation ${Transformation.currentMutation} was producing too much mutants.")
            }
        }
        return false // This is fine, no bugs.
    }

    override fun checkTextCompiling(text: String): Boolean =
            checkCompiling(PSICreator("").getPSIForText(text, false))

    private val databaseCapacity = 100
    private val maxMutationIterations = 2 * databaseCapacity

    private var overallMutants = 0

    fun clearOverallCounters() {
        overallMutants = 0
    }

    private val bugDatabase = BoundedSortedByModelElementSet(
        originalCoverage.copy(),
        databaseCapacity,
        Comparator { _, _ -> (tempCosineDistance * 10E7).toInt() },
        isSortingReversed = true
    )

    val numberOfBugs get() = bugDatabase.size

    private val successDatabase = BoundedSortedByModelElementSet(
        originalCoverage.copy(),
        databaseCapacity,
        Comparator { _, _ -> (tempCosineDistance * 10E7).toInt() },
        isSortingReversed = false
    )

    val numberOfSuccesses get() = successDatabase.size

    val executionStatistics: ExecutionStatistics
        get() = ExecutionStatistics.compose(originalCoverage, bugDatabase.toList(), successDatabase.toList())

    private val logger: Logger = Logger.getLogger("mutatorLogger")

}