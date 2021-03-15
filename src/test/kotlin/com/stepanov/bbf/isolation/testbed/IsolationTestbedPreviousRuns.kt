package com.stepanov.bbf.isolation.testbed

import com.stepanov.bbf.bugfinder.isolation.BugInfo
import com.stepanov.bbf.bugfinder.isolation.CoveragesForIsolation
import com.stepanov.bbf.bugfinder.isolation.BugIsolator
import com.stepanov.bbf.bugfinder.isolation.MutantsForIsolation
import com.stepanov.bbf.bugfinder.isolation.formulas.OchiaiRankingFormula
import com.stepanov.bbf.bugfinder.manager.BugType

fun isolationRefactoringInitialTest() {
    val localizer = BugIsolator(
        BugIsolator.typicalMutations,
        OchiaiRankingFormula,
        shouldResultsBeSerialized = true
    )

    val sampleFilePath = "src/test/resources/samples/test.kt"

    // first run in its entirety
    BugIsolator.mutantsExportTag = "testMutantsTag"
    val results1 = localizer.isolate(sampleFilePath, BugInfo(BugType.BACKEND, listOf("JVM" to "")))

    // second run starting from mutants
    val mutantsFilePath = "${BugIsolator.serializationDirPath}/$sampleFilePath-mutants-testMutantsTag"
    val mutants = MutantsForIsolation.import(mutantsFilePath)

    BugIsolator.coveragesExportTag = "testCoveragesTag"
    val results2 = localizer.isolate(mutants, BugInfo(BugType.BACKEND, listOf("JVM" to "")))

    // third run starting from coverage
    val coveragesFilePath = "${BugIsolator.serializationDirPath}/$sampleFilePath-coverages-testMutantsTag-testCoveragesTag"
    val coverages = CoveragesForIsolation.import(coveragesFilePath)

    BugIsolator.resultsExportTag = "testResultsTag"
    val results3 = localizer.isolate(coverages)

    println("results1 and results2 are ${if (results1 == results2) "equal" else "different"}!")
    println("results2 and results3 are ${if (results2 == results3) "equal" else "different"}!")
    println("results1 and results3 are ${if (results1 == results3) "equal" else "different"}!")
}