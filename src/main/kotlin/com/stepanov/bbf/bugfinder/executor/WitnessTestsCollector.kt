package com.stepanov.bbf.bugfinder.executor

import com.stepanov.bbf.bugfinder.isolation.MutantCoverages
import com.stepanov.bbf.bugfinder.isolation.MutationStatistics
import com.stepanov.bbf.bugfinder.manager.BugType
import com.stepanov.bbf.bugfinder.mutator.transformations.Transformation
import com.stepanov.bbf.bugfinder.mutator.transformations.Transformation.Companion.file
import com.stepanov.bbf.bugfinder.util.BoundedSortedByModelElementSet
import com.stepanov.bbf.coverage.CompilerInstrumentation
import com.stepanov.bbf.coverage.ExecutionCoverage
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

    private fun compile(text: String): Pair<Boolean, ExecutionCoverage?> {
        val status = checker.checkTest(text)
        var coverage: ExecutionCoverage? = null
        if (!CompilerInstrumentation.isEmpty) {
            coverage = ExecutionCoverage.createFromRecords()
            CompilerInstrumentation.clearRecords()
        }
        return status to coverage
    }

    // TODO Performance statistics (average per-compilation time and total isolation time).
    // TODO Statistics on how useful every mutation is.

    private val originalCoverage: ExecutionCoverage
    init {
        val (status, coverage) = compile(file.text)
        if (!status || coverage == null) throw IllegalArgumentException("")
        originalCoverage = coverage
    }

    // TODO Actual cosine similarity.
    private var tempCosineDistance: Double = 0.0

    override fun checkCompiling(file: KtFile): Boolean {
        val (status, coverage) = compile(file.text)
        if (coverage != null) {
            // tempCosineDistance = 1 - originalCoverage.cosineSimilarity(coverage)
            if (status) {
                bugDatabase.add(coverage.copy())
            } else {
                successDatabase.add(coverage.copy())
            }
        }
        return false // This is fine, no bugs.
    }

    override fun checkTextCompiling(text: String): Boolean = checkCompiling(PSICreator("").getPSIForText(text, false))

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

//    val mutantCoverages: MutantCoverages
//        get() {
//            val mutantCoverages = MutantCoverages()
//            mutantCoverages.original = originalCoverage
//            mutantCoverages.failureCoverages = failureDatabase.toMutableList()
//            mutantCoverages.successCoverages = successDatabase.toMutableList()
//            return mutantCoverages
//        }
//
//    val mutationStatistics = mutableMapOf<String, MutationStatistics>()
//
//    val failureCoverages: MutableList<Coverage>
//        get() = failureDatabase.toMutableList()
//
//    val successCoverages: MutableList<Coverage>
//        get() = successDatabase.toMutableList()

}