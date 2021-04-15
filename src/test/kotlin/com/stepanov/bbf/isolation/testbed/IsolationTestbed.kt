package com.stepanov.bbf.isolation.testbed

import com.stepanov.bbf.bugfinder.mutator.transformations.Transformation
import com.stepanov.bbf.reduktor.parser.PSICreator
import org.apache.log4j.PropertyConfigurator

fun main() {
    Transformation.file = PSICreator("").getPSIForText("")

    PropertyConfigurator.configure("src/main/resources/bbfLog4j.properties")
    PropertyConfigurator.configure("src/main/resources/reduktorLog4j.properties")

    reinitializeRandom(utilRandomSeed)

    // DATASET FILTERING TOOLS
//    filterSamplesByIdentity("isolation-evaluation/samples/youtrack")
//    filterSamplesByBugPresence("isolation-evaluation/samples/youtrack", defaultBugInfo)
//    reduceAllSamplesInDataset("isolation-evaluation/samples/youtrack", defaultBugInfo)

//    MutantGenerator.generate(
//        "isolation-evaluation/samples/youtrack",
//        mutantsExportTag = "default",
//        mutations = BugIsolator.typicalMutations
//    )

//    stacktraceEvaluation("isolation-evaluation/samples/youtrack-reduced")

    bugIsolationEvaluation(
        "isolation-evaluation/samples/youtrack", // choose original set or reduced one
        BugIsolationSourceType.MUTANTS,
        fractionOfRankingsConsidered = 1.0, // also a variable, duh
        mutantsImportTag = "default",
//        coveragesImportTag = "methods",
//        resultsImportTag = "Ochiai",
//        mutantsExportTag = "",
        coveragesExportTag = "methods", // mark coverage type (methods/branches)
        resultsExportTag = "Ochiai", // mark suspiciousness evaluation method)
        comparisonsTag = "100%-ranking"
    )

    // CALCULATING F-SCORES FOR STACKTRACE SIMILARITY RANKINGS
//    val estimatesOriginal = loadEstimationResults("isolation-evaluation/samples/youtrack-results/stacktraces.cbor")
//    val performancesOriginal = estimatesOriginal.calculateAllPossibleFScores(0.5)
}
