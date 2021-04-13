package com.stepanov.bbf.isolation.testbed

import com.stepanov.bbf.bugfinder.isolation.BugIsolator
import com.stepanov.bbf.bugfinder.isolation.CoveragesForIsolation
import com.stepanov.bbf.bugfinder.isolation.MutantsForIsolation
import com.stepanov.bbf.bugfinder.isolation.RankedProgramEntities
import java.io.File

var currentBugIsolator: BugIsolator? = null

enum class BugIsolationSourceType {
    SAMPLE, MUTANTS, COVERAGES, RESULTS;
}
var currentSourceType = BugIsolationSourceType.SAMPLE

var currentMutantsImportTag = ""
var currentCoveragesImportTag = ""
var currentResultsImportTag = ""

fun getActualSourceFilePath(path: String): String {
    val parentDir = path.replaceFirst("samples", "serialization").replace(".kt", "")
    return when (currentSourceType) {
        BugIsolationSourceType.SAMPLE ->
            path
        BugIsolationSourceType.MUTANTS ->
            "$parentDir/mutants-$currentMutantsImportTag.cbor"
        BugIsolationSourceType.COVERAGES ->
            "$parentDir/coverages-$currentMutantsImportTag-$currentCoveragesImportTag.cbor"
        BugIsolationSourceType.RESULTS ->
            "$parentDir/results-$currentMutantsImportTag-$currentCoveragesImportTag-$currentResultsImportTag.json"
    }
}

fun isolateBug(sourceFilePath: String, sample: Sample): RankedProgramEntities? {
    val bugIsolator = currentBugIsolator ?: throw IllegalStateException("isolator should be initialized before running isolations")

    isolationTestbedLogger.debug("started isolating ${getActualSourceFilePath(sourceFilePath)}")

    val ranking = when (currentSourceType) {
        BugIsolationSourceType.SAMPLE -> {
            File("tmp/tmp.kt").writeText(File(sourceFilePath).readText())
            bugIsolator.isolate(
                "tmp/tmp.kt", defaultBugInfo,
                serializationTag = if (bugIsolator.shouldResultsBeSerialized) sample.joinToString("/") else null
            )
        }
        BugIsolationSourceType.MUTANTS -> {
            val mutants = MutantsForIsolation.import(getActualSourceFilePath(sourceFilePath))
            bugIsolator.isolate(
                mutants, defaultBugInfo,
                serializationTag = if (bugIsolator.shouldResultsBeSerialized) sample.joinToString("/") else null
            )
        }
        BugIsolationSourceType.COVERAGES -> {
            val coverages = CoveragesForIsolation.import(getActualSourceFilePath(sourceFilePath))
            bugIsolator.isolate(
                coverages,
                serializationTag = if (bugIsolator.shouldResultsBeSerialized) sample.joinToString("/") else null
            )
        }
        BugIsolationSourceType.RESULTS -> {
            RankedProgramEntities.import(getActualSourceFilePath(sourceFilePath))
        }
    }

    isolationTestbedLogger.debug("finished isolating ${getActualSourceFilePath(sourceFilePath)}")
    isolationTestbedLogger.debug("")

    return ranking
}

var currentFractionOfRankingsConsidered: Double? = null

fun compareIsolationRankings(first: RankedProgramEntities, second: RankedProgramEntities): Double {
    val fraction = currentFractionOfRankingsConsidered
    val actualFirst = fraction?.let { first.topFraction(fraction) } ?: first
    val actualSecond = fraction?.let { second.topFraction(fraction) } ?: second
    return actualFirst.cosineSimilarity(actualSecond)
}