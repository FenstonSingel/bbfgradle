package com.stepanov.bbf.isolation.testbed

import com.stepanov.bbf.bugfinder.isolation.CoveragesForIsolation
import com.stepanov.bbf.bugfinder.isolation.BugIsolator
import com.stepanov.bbf.bugfinder.isolation.MutantsForIsolation
import com.stepanov.bbf.bugfinder.isolation.formulas.OchiaiRankingFormula

fun isolationRefactoringInitialTest() {
    val serializationDirPath = "isolation-evaluation/serialization"

    val bugIsolator = BugIsolator(
        BugIsolator.typicalMutations,
        OchiaiRankingFormula,
        shouldResultsBeSerialized = true,
        serializationDirPath = serializationDirPath
    )

    val sampleFilePath = "isolation-evaluation/samples/test.kt"
    val serializationTag = "testrun"

    // first run in its entirety
    bugIsolator.mutantsExportTag = "testMutantsTag"
    val results1 = bugIsolator.isolate(
        sampleFilePath, defaultBugInfo,
        serializationTag = serializationTag
    )

    // second run starting from mutants
    bugIsolator.coveragesExportTag = "testCoveragesTag"
    val mutantsFilePath = "$serializationDirPath/$serializationTag/mutants-testMutantsTag.cbor"
    val mutants = MutantsForIsolation.import(mutantsFilePath)
    val results2 = bugIsolator.isolate(
        mutants, defaultBugInfo,
        serializationTag = serializationTag
    )

    // third run starting from coverage
    bugIsolator.resultsExportTag = "testResultsTag"
    val coveragesFilePath = "$serializationDirPath/$serializationTag/coverages-testMutantsTag-testCoveragesTag.cbor"
    val coverages = CoveragesForIsolation.import(coveragesFilePath)
    val results3 = bugIsolator.isolate(
        coverages,
        serializationTag = serializationTag
    )

    println("results1 and results2 are ${if (results1 == results2) "equal" else "different"}!")
    println("results2 and results3 are ${if (results2 == results3) "equal" else "different"}!")
    println("results1 and results3 are ${if (results1 == results3) "equal" else "different"}!")
}

fun stacktraceEvaluation(datasetDirPath: String) {
    estimateSimilaritiesForSamplesInDataset(
        datasetDirPath,
        "stacktraces",
        ::getStacktrace,
        ::compareStacktraces
    )
}

fun bugIsolationEvaluation(
    datasetDirPath: String,
    sourceType: BugIsolationSourceType,
    mutantsImportTag: String,
    coveragesImportTag: String,
    mutantsExportTag: String,
    coveragesExportTag: String,
    resultsExportTag: String
) {
    currentSourceType = sourceType
    currentMutantsImportTag = mutantsImportTag     // relevant if source type is MUTANTS OR COVERAGES
    currentCoveragesImportTag = coveragesImportTag // relevant if source type is COVERAGES

    currentBugIsolator = BugIsolator(
        BugIsolator.typicalMutations,
        OchiaiRankingFormula,
        shouldResultsBeSerialized = true,
        serializationDirPath = datasetDirPath.replaceFirst("samples", "serialization")
    ).apply {
        this.mutantsExportTag = mutantsExportTag
        this.coveragesExportTag = coveragesExportTag
        this.resultsExportTag = resultsExportTag
    }

    fun composeActualExportTag(): String {
        return when (currentSourceType) {
            BugIsolationSourceType.SAMPLE -> "$mutantsExportTag-$coveragesExportTag-$resultsExportTag"
            BugIsolationSourceType.MUTANTS -> "$currentMutantsImportTag-$coveragesExportTag-$resultsExportTag"
            BugIsolationSourceType.COVERAGES -> "$currentMutantsImportTag-$currentCoveragesImportTag-$resultsExportTag"
        }
    }

    estimateSimilaritiesForSamplesInDataset(
        datasetDirPath,
        "FL-${composeActualExportTag()}",
        ::isolateBug,
        ::compareIsolationRankings
    )
}