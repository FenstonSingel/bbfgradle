package com.stepanov.bbf.bugfinder.executor

import com.stepanov.bbf.bugfinder.isolation.MutantCoverages
import com.stepanov.bbf.bugfinder.isolation.MutationStatistics
import com.stepanov.bbf.bugfinder.manager.BugType
import com.stepanov.bbf.bugfinder.mutator.transformations.Transformation
import com.stepanov.bbf.bugfinder.mutator.transformations.Transformation.Companion.file
import com.stepanov.bbf.bugfinder.util.BoundedSortedByModelElementSet
import com.stepanov.bbf.coverage.data.Coverage
import com.stepanov.bbf.coverage.data.EntityType
import com.stepanov.bbf.coverage.data.SegmentType
import com.stepanov.bbf.coverage.extraction.CoverageComposer
import com.stepanov.bbf.reduktor.parser.PSICreator
import org.jetbrains.kotlin.psi.KtFile

class WitnessTestsCollector(
    bugType: BugType,
    compilers: List<CommonCompiler>
) : Checker() {

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

    private val entityType = EntityType.METHODS
    private val segmentType = SegmentType.METHODS

    private val originalCoverage: Coverage
    init {
        val executionData = checker.getExecutionDataWithStatus(file.text).second
        originalCoverage = CoverageComposer.composeFrom(executionData, entityType, segmentType, false)
    }

    private lateinit var tempCoverage: Coverage
    private var tempCosineDistance: Double = 0.0

    override fun checkCompiling(file: KtFile): Boolean {
        val executionDataWithStatus = checker.getExecutionDataWithStatus(file.text)
        tempCoverage = CoverageComposer.composeFrom(executionDataWithStatus.second, entityType, segmentType, false)
        tempCosineDistance = 1 - originalCoverage.cosineSimilarity(tempCoverage)
        if (executionDataWithStatus.first) {
            mutationStatistics
                .getOrPut(Transformation.currentMutation) { MutationStatistics(Transformation.currentMutation) }
                .failures += tempCosineDistance
            failureDatabase.add(tempCoverage.copy())
        } else {
            mutationStatistics
                .getOrPut(Transformation.currentMutation) { MutationStatistics(Transformation.currentMutation) }
                .successes += tempCosineDistance
            successDatabase.add(tempCoverage.copy())
        }
        return false // This is fine, no bugs.
    }

    override fun checkTextCompiling(text: String): Boolean = checkCompiling(PSICreator("").getPSIForText(text, false))

    private val failureDatabase = BoundedSortedByModelElementSet(
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

    val mutantCoverages: MutantCoverages
        get() {
            val mutantCoverages = MutantCoverages()
            mutantCoverages.original = originalCoverage
            mutantCoverages.failureCoverages = failureDatabase.toMutableList()
            mutantCoverages.successCoverages = successDatabase.toMutableList()
            return mutantCoverages
        }

    val mutationStatistics = mutableMapOf<String, MutationStatistics>()

    val failureCoverages: MutableList<Coverage>
        get() = failureDatabase.toMutableList()

    val successCoverages: MutableList<Coverage>
        get() = successDatabase.toMutableList()

}