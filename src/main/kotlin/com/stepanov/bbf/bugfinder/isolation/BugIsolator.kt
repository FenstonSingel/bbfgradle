package com.stepanov.bbf.bugfinder.isolation

import com.stepanov.bbf.bugfinder.executor.*
import com.stepanov.bbf.bugfinder.manager.BugType
import com.stepanov.bbf.bugfinder.mutator.transformations.*
import com.stepanov.bbf.coverage.CompilerInstrumentation
import com.stepanov.bbf.coverage.ProgramCoverage
import com.stepanov.bbf.reduktor.executor.CompilerTestChecker
import com.stepanov.bbf.reduktor.parser.PSICreator
import org.apache.log4j.Logger
import org.jetbrains.kotlin.psi.KtFile
import java.io.File

class BugIsolator(
        private val mutations: List<Transformation>,
        private val rankingFormula: RankingFormula,
        private val shouldResultsBeSerialized: Boolean
) : Checker() {

    fun isolate(
            sampleFilePath: String, bugInfo: BugInfo,
            createChecker: (() -> CompilerTestChecker)? = null
    ): RankedProgramEntities {
        currentChecker = createChecker?.invoke() ?: constructChecker(bugInfo)

        // sometimes PSICreator trips up badly and there's nothing we can do about it
        val initialFile: KtFile
        try {
            initialFile = psiCreator.getPSIForFile(sampleFilePath)
        } catch (e: Throwable) {
            throw PSICreatorException(e)
        }

        // getting rid of possible leftovers from somewhere else
        CompilerInstrumentation.clearRecords()

        // since Kotlin compiler is stateful,
        // we spend some small amount of time finding the minimal original coverage
        val initCoverages = mutableListOf<ProgramCoverage>()
        for (i in 0 until originalSampleRecompilationTimes) {
            val currentCheckerRef = createChecker?.invoke() ?: constructChecker(bugInfo)
            val (isBugPresent, coverage) = compile(initialFile.text, currentCheckerRef)
            if (!isBugPresent) throw NoBugFoundException("$sampleFilePath contains no described bugs on used compiler version.")
            if (coverage != null) initCoverages.add(coverage)
        }
        val originalCoverage = initCoverages.minBy { c -> c.size } ?: throw IllegalStateException("No coverage was generated for $sampleFilePath for unknown reason.")

        // setting up the Transformation environment
        // the checker ref should not change throughout the entire bug isolation process
        Transformation.file = initialFile
        Transformation.checker = this

        // setting up this class's environment
        buggedCoverages = mutableSetOf()
        bugFreeCoverages = mutableSetOf()
        mutantsCatalog = mutableListOf()

        // generating mutants and their coverages for following fault localization
        for (mutation in mutations) {
            try {
                currentMutation = mutation.name
                mutation.transform()
            } catch (e: Throwable) {
                // if something bad happens when we mutate, we just
                // halt a particular mutation and turn to the next one
                logger.debug(e.message)
            }
        }

        val executionStatistics = ExecutionStatistics.compose(originalCoverage, buggedCoverages, bugFreeCoverages)
        val rankedProgramEntities = RankedProgramEntities.rank(executionStatistics, rankingFormula)

        // serializing intermediate and final results for later use if necessary
        if (shouldResultsBeSerialized) {
            File("$serializationDirPath/$sampleFilePath").parentFile.mkdirs()
            MutantsForIsolation(sampleFilePath, mutantsExportTag, initialFile.text, mutantsCatalog).export(
                    "$serializationDirPath/$sampleFilePath-mutants-$mutantsExportTag"
            )
            val coveragesFullExportTag = "$mutantsExportTag-$coveragesExportTag"
            CoveragesForIsolation(
                    sampleFilePath, coveragesFullExportTag,
                    originalCoverage, buggedCoverages.toList(), bugFreeCoverages.toList()
            ).export(
                    "$serializationDirPath/$sampleFilePath-coverages-$coveragesFullExportTag"
            )
            val resultsFullExportTag = "$coveragesFullExportTag-$resultsExportTag"
            rankedProgramEntities.export(
                    "$serializationDirPath/$sampleFilePath-results-$resultsFullExportTag"
            )
        }

        currentChecker = null // just introducing some consistency

        return rankedProgramEntities
    }

    fun isolate(
            mutants: MutantsForIsolation, bugInfo: BugInfo,
            createChecker: (() -> CompilerTestChecker)? = null
    ): RankedProgramEntities {
        // we don't need to use mutations so we can just use a local checker object
        val checker = createChecker?.invoke() ?: constructChecker(bugInfo)

        // sometimes PSICreator trips up badly and there's nothing we can do about it
        val initialFile: KtFile
        try {
            initialFile = psiCreator.getPSIForText(mutants.originalSample)
        } catch (e: Throwable) {
            throw PSICreatorException(e)
        }

        // getting rid of possible leftovers from somewhere else
        CompilerInstrumentation.clearRecords()

        // since Kotlin compiler is stateful,
        // we spend some small amount of time finding the minimal original coverage
        val initCoverages = mutableListOf<ProgramCoverage>()
        for (i in 0 until originalSampleRecompilationTimes) {
            val (isBugPresent, coverage) = compile(initialFile.text, checker)
            if (!isBugPresent) throw NoBugFoundException("Original sample contains no described bugs on used compiler version.")
            if (coverage != null) initCoverages.add(coverage)
        }
        val originalCoverage = initCoverages.minBy { c -> c.size } ?: throw IllegalStateException("No coverage was generated for original sample for unknown reason.")

        // setting up this class's environment
        val localBuggedCoverages = mutableSetOf<ProgramCoverage>()
        val localBugFreeCoverages = mutableSetOf<ProgramCoverage>()

        // generating mutants' coverages for following fault localization
        for (mutant in mutants.mutants) {
            val (isBugPresent, coverage) = compile(initialFile.text, checker)
            if (coverage != null) {
                if (isBugPresent)
                    localBuggedCoverages.add(coverage)
                else
                    localBugFreeCoverages.add(coverage)
            }
        }

        val executionStatistics = ExecutionStatistics.compose(originalCoverage, buggedCoverages, bugFreeCoverages)
        val rankedProgramEntities = RankedProgramEntities.rank(executionStatistics, rankingFormula)

        // serializing intermediate and final results for later use if necessary
        if (shouldResultsBeSerialized) {
            File("$serializationDirPath/${mutants.id}").parentFile.mkdirs()
            val coveragesFullExportTag = "${mutants.exportTag}-$coveragesExportTag"
            CoveragesForIsolation(
                    mutants.id, coveragesFullExportTag,
                    originalCoverage, buggedCoverages.toList(), bugFreeCoverages.toList()
            ).export(
                    "$serializationDirPath/${mutants.id}-coverages-$coveragesFullExportTag"
            )
            val resultsFullExportTag = "$coveragesFullExportTag-$resultsExportTag"
            rankedProgramEntities.export(
                    "$serializationDirPath/${mutants.id}-results-$resultsFullExportTag"
            )
        }

        return rankedProgramEntities
    }

    fun isolate(
            coverages: CoveragesForIsolation
    ): RankedProgramEntities {
        val executionStatistics = ExecutionStatistics.compose(coverages)
        val rankedProgramEntities = RankedProgramEntities.rank(executionStatistics, rankingFormula)

        // serializing final results for later use if necessary
        if (shouldResultsBeSerialized) {
            File("$serializationDirPath/${coverages.id}").parentFile.mkdirs()
            val resultsFullExportTag = "${coverages.exportTag}-$resultsExportTag"
            rankedProgramEntities.export(
                    "$serializationDirPath/${coverages.id}-results-$resultsFullExportTag"
            )
        }

        return rankedProgramEntities
    }

    override fun checkCompiling(file: KtFile): Boolean = checkTextCompiling(file.text)

    override fun checkTextCompiling(text: String): Boolean {
        val checker = currentChecker ?: throw IllegalStateException("Do not call checks from FaultLocalizer directly.")
        val (isBugPresent, coverage) = compile(text, checker)

        if (coverage != null) {
            val wasAdded = if (isBugPresent)
                buggedCoverages.add(coverage)
            else
                bugFreeCoverages.add(coverage)
            if (wasAdded) mutantsCatalog.add(text)
        }

        return false // keeping original sample mutating
    }

    // collections of coverages for until all mutations finish their execution
    private lateinit var buggedCoverages: MutableSet<ProgramCoverage>
    private lateinit var bugFreeCoverages: MutableSet<ProgramCoverage>

    // a collection of all interesting mutants in case we want to serialize them
    private lateinit var mutantsCatalog: MutableList<String>

    private val psiCreator = PSICreator("")

    // an oracle to determine whether a code mutant has a bug or not
    private var currentChecker: CompilerTestChecker? = null

    // a mutation's name, saved for debug purposes
    private lateinit var currentMutation: String

    // a distance function for BoundedSortedByModelElementSet instances
    private fun coverageDistanceFunction(model: ProgramCoverage, other: ProgramCoverage) =
            ((1 - model.cosineSimilarity(other)) * 1E8).toInt()

    private val logger = Logger.getLogger("isolationLogger")

    companion object {
        // different bugs need different oracles for fault localization
        // this function attempts to provide them
        fun constructChecker(bugInfo: BugInfo, filterInvalidCode: Boolean = false): CompilerTestChecker {
            val checker = when (bugInfo.type) {
                BugType.BACKEND, BugType.FRONTEND -> MultiCompilerCrashChecker(bugInfo.firstCompiler)
                // TODO check if next two checkers work correctly with this localizer
                BugType.DIFFBEHAVIOR -> DiffBehaviorChecker(bugInfo.compilers)
                BugType.DIFFCOMPILE -> DiffCompileChecker(bugInfo.compilers)
                BugType.UNKNOWN -> throw IllegalArgumentException("Unknown bug type detected.")
            }
            checker.filterInvalidCode = filterInvalidCode
            return checker
        }

        val typicalMutations = listOf(
                AddBlockToExpression(),
                AddBracketsToExpression(),
                AddDefaultValueToArg(),
                AddNotNullAssertions(),
                AddNullabilityTransformer(),
                ChangeOperators(),
                ChangeOperatorsToFunInvocations(),
                ChangeRandomASTNodes(),
                ChangeRandomASTNodesFromAnotherTrees(),
                ChangeRandomLines(),
                RemoveRandomASTNodes(),
                RemoveRandomLines()
        )

        var serializationDirPath = "tmp/isolation/serialized-results/tests"
        var mutantsExportTag: String = "default"
        var coveragesExportTag: String = "default"
        var resultsExportTag: String = "default"

        // magical constants which I don't know what to do with
        var originalSampleRecompilationTimes: Int = 4

        private fun compile(text: String, checker: CompilerTestChecker): Pair<Boolean, ProgramCoverage?> {
            val isBugPresent = checker.checkTest(text, "tmp/localization_tmp.kt")

            // absence of coverage should indicate we already stumbled upon this mutant before
            return if (!CompilerInstrumentation.isEmpty) {
                val coverage = ProgramCoverage.createFromProbes()
                CompilerInstrumentation.clearRecords() // making sure the previous comment holds
                isBugPresent to coverage
            } else {
                isBugPresent to null
            }
        }
    }

}