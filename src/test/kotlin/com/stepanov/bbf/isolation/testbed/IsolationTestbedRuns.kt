package com.stepanov.bbf.isolation.testbed

import com.stepanov.bbf.bugfinder.isolation.CoveragesForIsolation
import com.stepanov.bbf.bugfinder.isolation.BugIsolator
import com.stepanov.bbf.bugfinder.isolation.MutantsForIsolation
import com.stepanov.bbf.bugfinder.isolation.formulas.OchiaiRankingFormula

fun isolationRefactoringInitialTest() {
    val serializationDirPath = "src/test/resources/serialization"

    val bugIsolator = BugIsolator(
        BugIsolator.typicalMutations,
        OchiaiRankingFormula,
        shouldResultsBeSerialized = true,
        serializationDirPath = serializationDirPath
    )

    val sampleFilePath = "src/test/resources/samples/test.kt"
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

fun stacktraceEvaluation() {
    estimateSimilaritiesForSamplesInDataset(
        "src/test/resources/samples/youtrack",
        "stacktraces",
        ::getStacktrace,
        ::compareStacktraces
    )
}

fun bugIsolationEvaluation() {
    val datasetPath = "src/test/resources/samples/testrun"

    currentSourceType = BugIsolationSourceType.SAMPLE
    currentMutantsImportTag = ""   // relevant if source type is MUTANTS OR COVERAGES
    currentCoveragesImportTag = "" // relevant if source type is COVERAGES

    val newMutantsExportTag = ""
    val newCoveragesExportTag = ""
    val newResultsExportTag = ""

    currentBugIsolator = BugIsolator(
        BugIsolator.typicalMutations,
        OchiaiRankingFormula,
        shouldResultsBeSerialized = true,
        serializationDirPath = datasetPath.replaceFirst("samples", "serialization")
    ).apply {
        mutantsExportTag = newMutantsExportTag
        coveragesExportTag = newCoveragesExportTag
        resultsExportTag = newResultsExportTag
    }

    fun composeActualExportTag(): String {
        return when (currentSourceType) {
            BugIsolationSourceType.SAMPLE -> "$newMutantsExportTag-$newCoveragesExportTag-$newResultsExportTag"
            BugIsolationSourceType.MUTANTS -> "$currentMutantsImportTag-$newCoveragesExportTag-$newResultsExportTag"
            BugIsolationSourceType.COVERAGES -> "$currentMutantsImportTag-$currentCoveragesImportTag-$newResultsExportTag"
        }
    }

    estimateSimilaritiesForSamplesInDataset(
        datasetPath,
        "FL-${composeActualExportTag()}",
        ::isolateBug,
        ::compareIsolationRankings
    )
}