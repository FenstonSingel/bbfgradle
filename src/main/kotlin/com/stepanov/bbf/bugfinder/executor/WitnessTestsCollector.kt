package com.stepanov.bbf.bugfinder.executor

import com.stepanov.bbf.bugfinder.isolation.ExcessiveMutationException
import com.stepanov.bbf.bugfinder.isolation.ExecutionStatistics
import com.stepanov.bbf.bugfinder.isolation.NoBugFoundException
import com.stepanov.bbf.bugfinder.mutator.transformations.Transformation
import com.stepanov.bbf.bugfinder.mutator.transformations.Transformation.Companion.file
import com.stepanov.bbf.bugfinder.util.BoundedSortedByModelElementSet
import com.stepanov.bbf.coverage.CompilerInstrumentation
import com.stepanov.bbf.coverage.ProgramCoverage
import com.stepanov.bbf.reduktor.parser.PSICreator
import org.apache.log4j.Logger
import org.jetbrains.kotlin.psi.KtFile
import java.io.File

class WitnessTestsCollector(
    val compiler: CommonCompiler
) : Checker() {

    companion object {
        var databaseCapacity = Int.MAX_VALUE
        const val maxMutationIterations = 100
    }

    // list of things to note when porting the code to a refactored version
    // TODO Compilation timeouts (deleted in this branch) are a potential problem in the future.
    // TODO Check comments in MultiCompilerCrashCollector too.

    private val _instrPerformanceTimes = mutableListOf<Long>()
    val instrPerformanceTimes: List<Long> get() = _instrPerformanceTimes.toList()
    var numberOfCompilations = 0L
        private set

    val bugDistributionPerMutation = mutableMapOf<String, Pair<Long, Long>>()
    val successDistributionPerMutation = mutableMapOf<String, Pair<Long, Long>>()

    private val alreadyChecked = mutableMapOf<Int, Boolean>()

    private fun compile(text: String, saveResult: Boolean = true): Pair<Boolean, ProgramCoverage?> {
        val hash = text.hashCode()
        if (hash in alreadyChecked) {
            if (saveResult) logger.debug("(mutant was already checked)")
            return alreadyChecked[hash]!! to null
        }
        val file = File("tmp/tmp.kt")
        if (!file.exists()) file.createNewFile()
        val oldText = file.bufferedReader().readText()
        var writer = file.bufferedWriter()
        writer.write(text)
        writer.close()
        val status: Boolean
        status = try {
            compiler.isCompilerBug("tmp/tmp.kt")
        } catch (e: Throwable) {
            logger.debug(e)
            false
        } finally {
            writer = file.bufferedWriter()
            writer.write(oldText)
            writer.close()
        }
        if (saveResult) alreadyChecked[hash] = status
        var coverage: ProgramCoverage? = null
        if (!CompilerInstrumentation.isEmpty) {
            coverage = ProgramCoverage.createFromProbes()
        }
        return status to coverage
    }

    private val originalCoverage: ProgramCoverage
    init {
        compile(file.text, saveResult = false)
        val (status, coverage) = compile(file.text)
        if (!status || coverage == null) throw NoBugFoundException("A project should contain a bug in order to isolate it.")
        originalCoverage = coverage
    }

    private var tempCosineDistance: Double = 0.0

    override fun checkCompiling(file: KtFile): Boolean {
        logger.debug("Mutants by ${Transformation.currentMutation}: $overallMutants")

        compile(Transformation.file.text, saveResult = false)
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
            if (status) {
                bugDatabase.add(coverage.copy())
                bugCodeDatabase.add(file.text)
                currBugCodeDatabase.add(file.text)
            } else {
                successDatabase.add(coverage.copy())
                successCodeDatabase.add(file.text)
            }

            if (overallMutants >= maxMutationIterations) {
                throw ExcessiveMutationException("Mutation ${Transformation.currentMutation} was producing too much mutants.")
            }
        }
        return false // This is fine, no bugs.
    }

    override fun checkTextCompiling(text: String): Boolean =
            checkCompiling(PSICreator("").getPSIForText(text, false))

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

    private val bugCodeDatabase = BoundedSortedByModelElementSet(
        file.text,
        databaseCapacity,
        Comparator { _, _ -> (tempCosineDistance * 10E7).toInt() },
        isSortingReversed = true
    )

    val bugMutants get() = bugCodeDatabase.toList()

    val numberOfBugs get() = bugDatabase.size

    private val successDatabase = BoundedSortedByModelElementSet(
        originalCoverage.copy(),
        databaseCapacity,
        Comparator { _, _ -> (tempCosineDistance * 10E7).toInt() },
        isSortingReversed = false
    )

    private val successCodeDatabase = BoundedSortedByModelElementSet(
        file.text,
        databaseCapacity,
        Comparator { _, _ -> (tempCosineDistance * 10E7).toInt() },
        isSortingReversed = true
    )

    val successMutants get() = successCodeDatabase.toList()

    val numberOfSuccesses get() = successDatabase.size

    private val currBugCodeDatabase = BoundedSortedByModelElementSet(
        file.text,
        databaseCapacity,
        Comparator { _, _ -> (tempCosineDistance * 10E7).toInt() },
        isSortingReversed = true
    )

    fun clearCurrBugSamples() {
        currBugCodeDatabase.clear()
    }

    val currBestFailingMutant: String get() = currBugCodeDatabase.first()

    val executionStatistics: ExecutionStatistics
        get() = ExecutionStatistics.compose(originalCoverage, bugDatabase.toList(), successDatabase.toList())

    private val logger: Logger = Logger.getLogger("mutatorLogger")

}