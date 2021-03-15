package com.stepanov.bbf.isolation.testbed

import com.stepanov.bbf.bugfinder.mutator.transformations.Transformation
import com.stepanov.bbf.reduktor.parser.PSICreator
import org.apache.log4j.PropertyConfigurator

fun main() {
    Transformation.file = PSICreator("").getPSIForText("")

    PropertyConfigurator.configure("src/main/resources/bbfLog4j.properties")
    PropertyConfigurator.configure("src/main/resources/reduktorLog4j.properties")

    // DATASET FILTERING TOOLS
//    filterSamplesByIdentity("src/test/resources/samples/youtrack")
//    filterSamplesByBugPresence("src/test/resources/samples/youtrack", defaultBugInfo)
//    reduceAllSamplesInDataset("src/test/resources/samples/youtrack", defaultBugInfo)

    // GETTING STACKTRACE SIMILARITY RANKINGS FOR TWO DATASETS
//    estimateSimilaritiesForSamplesInDataset(
//        "src/test/resources/samples/youtrack",
//        "stacktraces",
//        ::getStacktrace,
//        ::compareStacktraces
//    )
//
//    estimateSimilaritiesForSamplesInDataset(
//        "src/test/resources/samples/youtrack-reduced",
//        "stacktraces",
//        ::getStacktrace,
//        ::compareStacktraces
//    )

    // CALCULATING F-SCORES FOR ACQUIRED STACKTRACE SIMILARITY RANKINGS
//    val estimatesOriginal = loadEstimationResults("src/test/resources/samples/youtrack-results/stacktraces.cbor")
//    val performancesOriginal = estimatesOriginal.calculateAllPossibleFScores(0.5)
//
//    val estimatesReduced = loadEstimationResults("src/test/resources/samples/youtrack-reduced-results/stacktraces.cbor")
//    val performancesReduced = estimatesReduced.calculateAllPossibleFScores(0.5)

    Unit
}
