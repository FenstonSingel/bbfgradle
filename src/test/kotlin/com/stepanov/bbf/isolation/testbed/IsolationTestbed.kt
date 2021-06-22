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

    // GENERATION OF SETS OF MUTANTS FOR EACH DATASET SAMPLE
//    MutantGenerator.generate(
//        "isolation-evaluation/samples/youtrack",
//        mutantsExportTag = "default",
//        mutations = BugIsolator.typicalMutations
//    )

//    stacktraceEvaluation("isolation-evaluation/samples/youtrack-reduced")

    // this is a fucking mess and i'm very sorry for bringing this into existence
    bugIsolationEvaluation(
        "isolation-evaluation/samples/youtrack-reduced", // choose original set or reduced one
        BugIsolationSourceType.MUTANTS, // pick a type of input data to work with
        fractionOfRankingsConsidered = 1.0, // determine which fraction of each suspiciousness ranking (from top to bottom)
                                            // is taken into consideration when comparing two bugs
        // fine-tune which input data you want to work with
        // make sure to specify all necessary tags for an input data type chosen earlier
        mutantsImportTag = "default",
//        coveragesImportTag = "methods",
//        resultsImportTag = "Ochiai",
//        mutantsExportTag = "",
        // mark resulting coverage type (methods/branches)
        // the actual coverage type should be changed manually in the java agent (recompile it as well)
        // (see com.stepanov.bbf.coverage.instrumentation.PremainClass for that)
        coveragesExportTag = "branches",
        // mark used suspiciousness evaluation method
        // the actual evaluation method should be changed manually
        // (see com.stepanov.bbf.isolation.testbed.bugIsolationEvaluation / IsolationTestbedRuns.kt::73 for that)
        resultsExportTag = "Ochiai",
        // mark resulting fraction of each suspiciousness ranking taken into consideration
        // the actual value was changed manually earlier
        comparisonsTag = "100%-ranking"
    )

    // CALCULATING F-SCORES FOR SIMILARITY RANKINGS
//    val estimatesOriginal = loadEstimationResults("isolation-evaluation/samples/youtrack-results/stacktraces.cbor")
//    val performancesOriginal = estimatesOriginal.calculateAllPossibleFScores(0.5)
}
