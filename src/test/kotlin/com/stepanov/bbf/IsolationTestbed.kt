package com.stepanov.bbf

import com.stepanov.bbf.bugfinder.Reducer
import com.stepanov.bbf.bugfinder.executor.MultiCompilerCrashChecker
import com.stepanov.bbf.bugfinder.executor.WitnessTestsCollector
import com.stepanov.bbf.bugfinder.executor.compilers.JVMCompiler
import com.stepanov.bbf.bugfinder.isolation.BugIsolator
import com.stepanov.bbf.bugfinder.isolation.NoBugFoundException
import com.stepanov.bbf.bugfinder.isolation.RankedProgramEntities
import com.stepanov.bbf.bugfinder.isolation.formulas.OchiaiRankingFormula
import com.stepanov.bbf.bugfinder.mutator.transformations.Transformation
import com.stepanov.bbf.coverage.CompilerInstrumentation
import com.stepanov.bbf.coverage.ProgramCoverage
import com.stepanov.bbf.reduktor.parser.PSICreator
import org.apache.log4j.Logger
import org.apache.log4j.PropertyConfigurator
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import kotlin.collections.*

fun main() {

    PropertyConfigurator.configure("src/main/resources/bbfLog4j.properties")
    PropertyConfigurator.configure("src/main/resources/reduktorLog4j.properties")

    println("Current coverage: ${CompilerInstrumentation.coverageType}")

    Transformation.file = PSICreator("").getPSIForFile("tmp/check_tmp.kt")
    bugChecker = MultiCompilerCrashChecker(getCompiler())

//    filterSameSamples("$samplesPath/$set")

//    val coverages1 = mutableListOf<ProgramCoverage>()
//    for (i in 0 until 10) {
//        reinitializeRandom(utilRandomSeed)
//        coverages1 += getCoverageWithCollector("/home/ruban/kotlin-samples/temp/2/1.kt", BugType.BACKEND, listOf(getCompiler()))
//    }
//
//    val coverages2 = mutableListOf<ProgramCoverage>()
//    for (i in 0 until 10) {
//        reinitializeRandom(utilRandomSeed)
//        coverages2 += getCoverageWithCollector("/home/ruban/kotlin-samples/temp/2/2.kt", BugType.BACKEND, listOf(getCompiler()))
//    }
//
//    val coverages3 = mutableListOf<ProgramCoverage>()
//    for (i in 0 until 10) {
//        reinitializeRandom(utilRandomSeed)
//        coverages3 += getCoverageWithCollector("/home/ruban/kotlin-samples/temp/2/1.kt", BugType.BACKEND, listOf(getCompiler()))
//    }

//    filterSameSamples("$samplesPath/$set")

//    filteringType = "stacktraces"
//
//    BugIsolator.clearStatistics()
//
//    evaluateOnDataset(
//        ::fetchErrorData,
//        ::compareStacktraces
//    )

//    reduceAll()

    reduceMode = false
    ProgramCoverage.shouldCoverageBeBinary = true
    WitnessTestsCollector.databaseCapacity = 100
    BugIsolator.rankingFormula = OchiaiRankingFormula

    filteringType = "fault-localization"

    BugIsolator.clearStatistics()

    evaluateOnDataset(
        ::isolateBug,
        ::compareIsolationRankings
    )

//    reduceMode = false
//    ProgramCoverage.shouldCoverageBeBinary = true
//    WitnessTestsCollector.databaseCapacity = 200
//    BugIsolator.rankingFormula = OchiaiRankingFormula
//
//    BugIsolator.clearStatistics()
//    reinitializeRandom(utilRandomSeed)
//    customTag = "try1"
//
//    evaluateOnDataset(
//        ::isolateBug,
//        ::compareIsolationRankings
//    )
//
//    BugIsolator.clearStatistics()
//    reinitializeRandom(utilRandomSeed)
//    customTag = "try2"
//
//    evaluateOnDataset(
//        ::isolateBug,
//        ::compareIsolationRankings
//    )
//
//    reduceMode = false
//    ProgramCoverage.shouldCoverageBeBinary = true
//    WitnessTestsCollector.databaseCapacity = 200
//    BugIsolator.rankingFormula = OchiaiRankingFormula
//
//    ProgramCoverage.shouldCoverageBeBinary = false
//
//    BugIsolator.clearStatistics()
//    reinitializeRandom(utilRandomSeed)
//    customTag = "try1"
//
//    evaluateOnDataset(
//        ::isolateBug,
//        ::compareIsolationRankings
//    )
//
//    BugIsolator.clearStatistics()
//    reinitializeRandom(utilRandomSeed)
//    customTag = "try2"
//
//    evaluateOnDataset(
//        ::isolateBug,
//        ::compareIsolationRankings
//    )
//
//    reduceMode = false
//    ProgramCoverage.shouldCoverageBeBinary = true
//    WitnessTestsCollector.databaseCapacity = 200
//    BugIsolator.rankingFormula = OchiaiRankingFormula
//
//    reduceMode = true
//
//    BugIsolator.clearStatistics()
//    reinitializeRandom(utilRandomSeed)
//    customTag = "try1"
//
//    evaluateOnDataset(
//        ::isolateBug,
//        ::compareIsolationRankings
//    )
//
//    BugIsolator.clearStatistics()
//    reinitializeRandom(utilRandomSeed)
//    customTag = "try2"
//
//    evaluateOnDataset(
//        ::isolateBug,
//        ::compareIsolationRankings
//    )

//    reduceMode = false
//    majorAttributes = listOf("fault-localization")
//    minorAttributes = listOf(coverageType)
//
//    evaluateOnDataset(
//        ::isolateBug,
//        ::compareIsolationRankings
//    )
//
//    reduceMode = true
//    majorAttributes = listOf("fault-localization")
//    minorAttributes = listOf(coverageType, "reduction")
//
//    evaluateOnDataset(
//        ::isolateBug,
//        ::compareIsolationRankings
//    )

}

val logger: Logger = Logger.getLogger("isolationTestbedLogger")

var filteringType: String = "fault-localization"

val coverageType = CompilerInstrumentation.coverageType.name.toLowerCase()
val binaryCoverageTag: String get() = if (ProgramCoverage.shouldCoverageBeBinary) "binary" else "non-binary"
val reductionTag: String get() = if (reduceMode || "reduced" in set) "reduced" else "not-reduced"
val mutantCapacityTag: String get() = "${WitnessTestsCollector.databaseCapacity}-mutants"
val formulaTag: String get() = BugIsolator.rankingFormula.name
var customTag = ""

const val utilRandomSeed = 56239485L

const val set = "filtered-ground-truth-w-subtasks-reduced"
val majorAttributes get() = listOf(filteringType)
val minorAttributes: List<String>
    get() {
        val list = if (filteringType == "stacktraces") mutableListOf("stacktraces") else mutableListOf(
            coverageType, binaryCoverageTag, reductionTag, mutantCapacityTag, formulaTag, "higherOrder"
        )
        if (customTag != "") list += customTag
        return list.toList()
    }
val tag: String get() = "$set/${majorAttributes.joinToString("-")}/${minorAttributes.joinToString("-")}"

const val samplesPath = "/home/ruban/kotlin-samples"
const val outputPath = "/home/ruban/isolation-statistics"
const val resultsPath = "$outputPath/results"

const val rankingsPath = "$outputPath/rankings"

fun printIsolationGlobalStatistics(log: (String) -> Unit) {
    log("Isolations: ${BugIsolator.numberOfIsolations}")
    log("Total isolation time: ${BugIsolator.totalIsolationTime}")
    log("Average isolation time: ${BugIsolator.meanIsolationTime}")
    log("Isolation time's standard deviation: ${BugIsolator.isolationTimeSD}")
    log("Minimum isolation time: ${BugIsolator.minIsolationTime}")
    log("Maximum isolation time: ${BugIsolator.maxIsolationTime}")
    log("")
    log("Compilations: ${BugIsolator.numberOfCompilations}")
    log("Time spent on instrumentation: ${CompilerInstrumentation.timeSpentOnInstrumentation}")
    log("Total coverage recording time: ${BugIsolator.totalInstrPerformanceTime}")
    log("Average coverage recording time: ${BugIsolator.meanInstrPerformanceTime}")
    log("")
    log("Total failing code samples generated: ${BugIsolator.totalFailingCodeSamples}")
    log("Average failing code samples generated: ${BugIsolator.meanFailingCodeSamples}")
    log("Total passing code samples generated: ${BugIsolator.totalPassingCodeSamples}")
    log("Average passing code samples generated: ${BugIsolator.meanPassingCodeSamples}")
    log("")
    log("Distribution of samples per mutation:")
    BugIsolator.codeSampleDistributionPerMutation.toSortedMap().forEach { key, (bugsRatio, successesRatio) ->
        log("   $key: %.2f%% fails and %.2f%% passes".format(bugsRatio * 100, successesRatio * 100))
    }
    log("")
    log("Quality of samples per mutation:")
    BugIsolator.codeSampleQualityPerMutation.toSortedMap().forEach { key, (bugsAvgDist, successesAvgDist) ->
        log("   $key: $bugsAvgDist avg. fail dist and $successesAvgDist avg. pass dist")
    }
}

// should be called as soon as the ranking was acquired
fun RankedProgramEntities.saveIsolationResults(name: String) {
    val fileName = "$rankingsPath/$tag/$name.ranking"
    File(fileName.substringBeforeLast('/')).mkdirs()
    File(fileName).printWriter().use { writer ->
        writer.println("failing mutants: ${BugIsolator.lastNumberOfFailingMutants}")
        writer.println("passing mutants: ${BugIsolator.lastNumberOfPassingMutants}")
        writer.println()
        for ((entity, rank) in toList()) {
            writer.println("$entity - $rank")
        }
        writer.println()
        writer.println("all failing mutants:")
        for (mutant in BugIsolator.lastFailingMutants) {
            writer.println("=".repeat(67))
            writer.println(mutant)
        }
        writer.println()
        writer.println("all passing mutants:")
        for (mutant in BugIsolator.lastPassingMutants) {
            writer.println("=".repeat(67))
            writer.println(mutant)
        }
    }
}

fun getCompiler() = JVMCompiler("")

lateinit var bugChecker: MultiCompilerCrashChecker
fun checkBugPresence(text: String): Boolean = bugChecker.checkTest(text, "tmp/check_tmp.kt")

data class Sample(val group: String, val id: String) {
    override fun toString(): String = "$group/$id"
}
class Comparison(val first: Sample, val second: Sample, val similarity: Double) {
    override fun toString(): String = "$first to $second: $similarity"
}
class Group(val innerComparisons: MutableList<Comparison> = mutableListOf(),
            val outerComparisons: MutableList<Comparison> = mutableListOf()) {
    override fun toString(): String =
        "inner: \n${innerComparisons.joinToString("\n")}\nouter: \n${outerComparisons.joinToString("\n")}"
}
class ThresholdStatistics {
    companion object {
        private const val beta = 0.5
    }

    var truePositives = 0
    var falsePositives = 0
    var trueNegatives = 0
    var falseNegatives = 0

    val precision get() = truePositives.toDouble() / (truePositives + falsePositives)

    val recall get() = truePositives.toDouble() / (truePositives + falseNegatives)

    val fScore: Double get() {
        val numerator = (1 + beta * beta) * truePositives
        val denominator = numerator + beta * beta * falseNegatives + falsePositives
        return numerator / denominator
    }

    override fun toString(): String =
        "P: %.2f%%, R: %.2f%%, F_$beta: %.2f%%".format(precision * 100, recall * 100, fScore * 100)
}

val regex = Regex("""([^/]+)/([^/]+)\.kt${'$'}""")

fun reduceAll() {
    val compiler = getCompiler()
    File("$samplesPath/$set")
        .walk().sortedBy { it.absolutePath }
        .forEach {
            val sourceFilePath = it.absolutePath
            regex.find(sourceFilePath)?.let { _ ->
                if (!checkBugPresence(File(sourceFilePath).readText())) {
                    logger.debug("$sourceFilePath has to have bugs in order to work with it")
                    logger.debug("")
                    return@let
                }
                File("tmp/tmp.kt").writeText(File(sourceFilePath).readText())
                try {
                    File("tmp/reduce_tmp.kt").writeText(File(sourceFilePath).readText())
                    val executor = Executors.newSingleThreadExecutor()
                    logger.debug("started reducing $sourceFilePath")
                    val future = executor.submit {
                        Reducer.reduce("tmp/reduce_tmp.kt", compiler)
                    }
                    try {
                        future.get(20, TimeUnit.MINUTES)
                    } catch (e: TimeoutException) {
                        logger.debug("timeout!!")
                        future.cancel(true)
                        executor.shutdownNow()
                        while (true) {
                            try {
                                if (executor.awaitTermination(2, TimeUnit.SECONDS)) break
                                executor.shutdownNow()
                            } catch (_: InterruptedException) {}
                        }
                        throw e
                    }
                    logger.debug("finished reducing $sourceFilePath")
                } catch (e: Throwable) {
                    logger.debug("Exception: ${e.javaClass.name}")
                    logger.debug(if (e.message.isNullOrEmpty()) "no message" else e.message)
                    logger.debug("$sourceFilePath not fully reduced")
                } finally {
                    val reducedText = File("tmp/reduce_tmp.kt").readText()
                    if (checkBugPresence(reducedText)) {
                        logger.debug("bug was preserved during reduction => saving reduced version")
                        File("tmp/tmp.kt").writeText(reducedText)
                    } else {
                        logger.debug("bug was not preserved during reduction => back to original version")
                    }
                    logger.debug("")
                }
                File(sourceFilePath.replace(set, "$set-reduced").substringBeforeLast("/")).mkdirs()
                File(sourceFilePath.replace(set, "$set-reduced")).writeText(File("tmp/tmp.kt").readText())
            }
        }
}

var reduceMode = false

val threshold: Double? = null

fun <T> evaluateOnDataset(
    evaluateSample: (String) -> T?,
    compareSamples: (T, T) -> Double
) {
    val noBugs = mutableListOf<String>()
    val unevaluated = mutableListOf<String>()

    val results = mutableListOf<Pair<Sample, T>>()
    val comparisons = mutableListOf<Comparison>()
    val groups = mutableMapOf<String, Group>()

    val thresholds = mutableMapOf<Double, ThresholdStatistics>()

    File("$resultsPath/$tag.log".substringBeforeLast('/')).mkdirs()
    fun intermediateResults() {
        File("$resultsPath/$tag.log").printWriter().use { writer ->
            for (comparison in comparisons) {
                writer.print(comparison)
                if (comparison.first.group == comparison.second.group) writer.print(" (duplicates)")
                writer.println()
            }
            writer.println()
//            writer.println()
//            for ((id, group) in groups) writer.println("$id\n$group\n")
//            writer.println()
            if (threshold == null) {
                val sortedThresholds = thresholds.map { (k, v) -> k to v }.sortedByDescending { (_, v) -> v.fScore }
                for ((threshold, statistics) in sortedThresholds) {
                    writer.println("$threshold: $statistics")
                }
            }
            writer.println()
            writer.println("Unevaluated:")
            for (file in unevaluated) {
                writer.println(file)
            }
            writer.println()
            writer.println("W/o bugs:")
            for (file in noBugs) {
                writer.println(file)
            }
            writer.println()
            printIsolationGlobalStatistics { writer.println(it) }
        }
    }

    val compiler = getCompiler()

    File("$samplesPath/$set")
        .walk().sortedBy { it.absolutePath }
        .forEach {
            val sourceFilePath = it.absolutePath
            regex.find(sourceFilePath)?.let { matchResult ->
                val sampleID = matchResult.groupValues
                val sample = Sample(sampleID[1], sampleID[2])
                if (!checkBugPresence(File(sourceFilePath).readText())) {
                    logger.debug("$sourceFilePath has to have bugs in order to work with it")
                    logger.debug("")
                    noBugs += sample.toString()
                    return@let
                }

                File("tmp/tmp.kt").writeText(File(sourceFilePath).readText())
                if (reduceMode) {
                    var isPartiallyReduced = false
                    try {
                        File("tmp/reduce_tmp.kt").writeText(File(sourceFilePath).readText())
                        val executor = Executors.newSingleThreadExecutor()
                        logger.debug("started reducing $sourceFilePath")
                        val future = executor.submit {
                            Reducer.reduce("tmp/reduce_tmp.kt", compiler)
                        }
                        try {
                            future.get(20, TimeUnit.MINUTES)
                        } catch (e: TimeoutException) {
                            logger.debug("timeout!!")
                            future.cancel(true)
                            executor.shutdownNow()
                            while (true) {
                                try {
                                    if (executor.awaitTermination(2, TimeUnit.SECONDS)) break
                                    executor.shutdownNow()
                                } catch (_: InterruptedException) {}
                            }
                            throw e
                        }
                        logger.debug("finished reducing $sourceFilePath")
                    } catch (e: Throwable) {
                        logger.debug("Exception: ${e.javaClass.name}")
                        logger.debug(if (e.message.isNullOrEmpty()) "no message" else e.message)
                        logger.debug("$sourceFilePath not fully reduced")
                        isPartiallyReduced = true
                    } finally {
                        val reducedText = File("tmp/reduce_tmp.kt").readText()
                        if (checkBugPresence(reducedText)) {
                            logger.debug("bug was preserved during reduction => using reduced version")
                            File("tmp/tmp.kt").writeText(reducedText)
                            if (isPartiallyReduced) partiallyReducedFiles += sourceFilePath
                        } else {
                            logger.debug("bug was not preserved during reduction => back to original version")
                            unreducedFiles += sourceFilePath
                        }
                        logger.debug("")
                    }
                }

                val evaluation = evaluateSample(sourceFilePath)
                evaluation?.let {
                    results.forEach { (oldSample, oldEvaluation) ->
                        fun updateThreshold(
                            threshold: Double, newValue: Double,
                            firstGroup: String, secondGroup: String,
                            outStatistics: ThresholdStatistics
                        ) {
                            if (newValue >= threshold) {
                                if (firstGroup == secondGroup) {
                                    outStatistics.truePositives += 1
                                } else {
                                    outStatistics.falsePositives += 1
                                }
                            } else {
                                if (firstGroup != secondGroup) {
                                    outStatistics.trueNegatives += 1
                                } else {
                                    outStatistics.falseNegatives += 1
                                }
                            }
                        }

                        val comparisonValue = compareSamples(evaluation, oldEvaluation)
                        if (threshold == null) {
                            thresholds.forEach { (threshold, statistics) ->
                                updateThreshold(threshold, comparisonValue, sample.group, oldSample.group, statistics)
                            }
                            if (comparisonValue !in thresholds) {
                                val statistics = ThresholdStatistics()
                                updateThreshold(
                                    comparisonValue, comparisonValue,
                                    sample.group, oldSample.group,
                                    statistics
                                )
                                thresholds[comparisonValue] = statistics
                                comparisons.forEach { comparison ->
                                    updateThreshold(
                                        comparisonValue, comparison.similarity,
                                        comparison.first.group, comparison.second.group,
                                        statistics
                                    )
                                }
                            }
                        }
                        val comparison = Comparison(sample, oldSample, comparisonValue)
                        comparisons += comparison
                        if (sample.group == oldSample.group) {
                            groups.getOrPut(sample.group) { Group() }.innerComparisons += comparison
                        } else {
                            groups.getOrPut(sample.group) { Group() }.outerComparisons += comparison
                            groups.getOrPut(oldSample.group) { Group() }.outerComparisons += comparison
                        }
                    }
                    results += sample to evaluation
                    comparisons.sortByDescending { comparison -> comparison.similarity }
                }
                if (evaluation == null) unevaluated += sample.toString()
                intermediateResults()
            }
        }
}

val unreducedFiles = mutableListOf<String>()
val partiallyReducedFiles = mutableListOf<String>()

fun isolateBug(sourceFilePath: String): RankedProgramEntities? {
    val compiler = getCompiler()

    logger.debug("started isolating $sourceFilePath")
    logger.debug("")
    val ranking: RankedProgramEntities?
    try {
        ranking = BugIsolator.isolate("tmp/tmp.kt", compiler)
        ranking?.let {
            val sourceFile = File(sourceFilePath)
            ranking.saveIsolationResults("${sourceFile.parentFile.name}/${sourceFile.nameWithoutExtension}")
            printIsolationGlobalStatistics { logger.debug(it) }
            logger.debug("")
        }
    } catch (e: Throwable) {
        logger.debug("Exception: ${e.javaClass.name}")
        logger.debug(if (e.message.isNullOrEmpty()) "no message" else e.message)
        return null
    }
    logger.debug("finished isolating $sourceFilePath")
    logger.debug("")
    return ranking
}

fun compareIsolationRankings(first: RankedProgramEntities, second: RankedProgramEntities): Double {
    return first.cosineSimilarity(second)
}

fun fetchErrorData(sourceFilePath: String): String? {
    logger.debug("compiling $sourceFilePath to get stacktrace data")
    logger.debug("code:\n${File("tmp/tmp.kt").readText()}")
    logger.debug("")
    val stacktrace = getCompiler().getErrorMessage("tmp/tmp.kt")
        .split("Cause:")
        .last()
        .split("\n")
        .map { it.trim() }
        .filter { it.startsWith("at ") }
        .joinToString("\n") { it.replaceFirst("at ", "") }
    logger.debug("stacktrace for $sourceFilePath obtained successfully")
    logger.debug("")
    return stacktrace
}

private val diffMatchPatch = DiffMatchPatch()

fun compareStacktraces(first: String, second: String): Double {
    val normalizingDivider = first.length + second.length
    if (normalizingDivider == 0) return 1.0
    val diffs = diffMatchPatch.diffMain(first, second)
    val similarity = diffs
        .filter { diff -> diff.operation.name == "EQUAL" }
        .fold(0) { acc, diff -> acc + diff.text.length }
    return 2 * similarity.toDouble() / normalizingDivider
}

//fun compareRankings(file: File, outputPath: String, reduceMode: Boolean = false) {
//    val regex = Regex("""([^/]+)/([^/]+)\.kt${'$'}""")
//    val rankings = mutableMapOf<Sample, RankedProgramEntities>()
//
//    fun intermediateResults() {
//        printIsolationGlobalStatistics { logger.debug(it) }
//        val comparisons = mutableListOf<Triple<Sample, Sample, Double>>()
//        for ((sample1, ranking1) in rankings) {
//            for ((sample2, ranking2) in rankings) {
//                if (sample1 != sample2 && comparisons.find { (s1, s2, _) -> s1 == sample2 && s2 == sample1 } == null) {
//                    comparisons += Triple(sample1, sample2, ranking1.cosineSimilarity(ranking2))
//                }
//            }
//        }
//        var uniquesComparisons = 0
//        var duplicatesComparisons = 0
//        var uniquesDivergence = 0.0
//        var duplicatesDivergence = 0.0
//        val threshold = 0.8
//        var uniquesDivRelativeToT = 0
//        var duplicatesDivRelativeToT = 0
//        var firstUniques: Int? = null
//        var firstUniquesValue = 0.0
//        var lastDuplicates: Int? = null
//        var lastDuplicatesValue = 0.0
//        File(outputPath.substringBeforeLast('/')).mkdirs()
//        File(outputPath).printWriter().use { writer ->
//            var index = 1
//            for ((sample1, sample2, similarity) in comparisons.sortedByDescending { (_, _, ranking) -> ranking }) {
//                writer.print("$sample1 to $sample2: $similarity ")
//                if (sample1.group == sample2.group) {
//                    writer.println("(DUPLICATES)")
//                    ++duplicatesComparisons
//                    lastDuplicates = index
//                    lastDuplicatesValue = similarity
//                    duplicatesDivergence += 1 - similarity
//                    if (similarity <= threshold) ++duplicatesDivRelativeToT
//                } else {
//                    writer.println()
//                    ++uniquesComparisons
//                    if (firstUniques == null) {
//                        firstUniques = index
//                        firstUniquesValue = similarity
//                    }
//                    uniquesDivergence += similarity
//                    if (similarity > threshold) ++uniquesDivRelativeToT
//                }
//                ++index
//            }
//            writer.println()
//            writer.println("Current coverage: ${CompilerInstrumentation.coverageType}")
//            writer.println("Worst-ranked duplicates: $lastDuplicates")
//            writer.println("(value: $lastDuplicatesValue)")
//            writer.println("Worst-ranked uniques: $firstUniques")
//            writer.println("(value: $firstUniquesValue)")
//            writer.println("Range of mixed entries: ${(firstUniques?.let { lastDuplicates?.minus(it) }) ?: "none"}")
//            writer.println("Absolute duplicates divergence: $duplicatesDivergence")
//            writer.println("(normalized: ${duplicatesDivergence / duplicatesComparisons})")
//            writer.println("Absolute uniques divergence: $uniquesDivergence")
//            writer.println("(normalized: ${uniquesDivergence / uniquesComparisons})")
//            writer.println("Duplicates divergence relative to $threshold: $duplicatesDivRelativeToT")
//            writer.println("(normalized: ${duplicatesDivRelativeToT.toDouble() / duplicatesComparisons})")
//            writer.println("Uniques divergence relative to $threshold: $uniquesDivRelativeToT")
//            writer.println("(normalized: ${uniquesDivRelativeToT.toDouble() / uniquesComparisons})")
//            writer.println("Total divergence: ${duplicatesDivergence + uniquesDivergence}")
//            writer.println("(normalized: ${(duplicatesDivergence + uniquesDivergence) / (duplicatesComparisons + uniquesComparisons)})")
//        }
//    }
//
//    val unreducedFiles = mutableListOf<String>()
//    val filesWithNoBugs = mutableListOf<String>()
//    val fuckedUpFiles = mutableListOf<String>()
//    file.walk().sortedBy { it.absolutePath }.forEach {
//        val sourceFilePath = it.absolutePath
//        val matchResult = regex.find(sourceFilePath)
//        if (matchResult != null) {
//            val ids = matchResult.groupValues
//            val sample = Sample(ids[1], ids[2].toInt())
//            // anything of interest got nuked from here during refactoring
//        }
//    }
//}

//fun checkStability(file: File, numberOfSamples: Int, numberOfIterations: Int, outputPath: String) {
//    val regex = Regex("""/([^/]+)\.kt${'$'}""")
//    val comparisons = mutableMapOf<String, List<Double>>()
//    val allFailingMutants = mutableMapOf<String, List<Int>>()
//    val allPassingMutants = mutableMapOf<String, List<Int>>()
//    val filesWithNoBugs = mutableListOf<String>()
//    val fuckedUpFiles = mutableListOf<String>()
//    file.walk().filter { it.isFile }.toList().shuffled().take(numberOfSamples).forEach { sample ->
//        val sourceFilePath = sample.absolutePath
//        val matchResult = regex.find(sourceFilePath)
//        if (matchResult != null) {
//            val rankings = mutableListOf<RankedProgramEntities>()
//            val failingMutants = mutableListOf<Int>()
//            val passingMutants = mutableListOf<Int>()
//            for (x in 0 until numberOfIterations) {
//                logger.debug("started isolating $sourceFilePath")
//                try {
//                    val ranking = BugIsolator.isolate(sourceFilePath, BugType.BACKEND)
//                    if (ranking != null) {
//                        ranking.saveIsolationResults("${sample.parentFile.name}/${sample.nameWithoutExtension}/$x")
//                        printIsolationGlobalStatistics { logger.debug(it) }
//                        rankings += ranking
//                        failingMutants += BugIsolator.lastNumberOfFailingMutants
//                        passingMutants += BugIsolator.lastNumberOfPassingMutants
//                    } else {
//                        filesWithNoBugs += sourceFilePath
//                    }
//                } catch (e: Throwable) {
//                    logger.debug(e.message)
//                    fuckedUpFiles += sourceFilePath
//                }
//                logger.debug("finished isolating $sourceFilePath")
//                logger.debug("")
//            }
//            val localComparisons = mutableListOf<Double>()
//            for (i in 0 until rankings.size) {
//                for (j in i + 1 until rankings.size) {
//                    localComparisons += rankings[i].cosineSimilarity(rankings[j])
//                }
//            }
//            comparisons[sourceFilePath] = localComparisons
//            allFailingMutants[sourceFilePath] = failingMutants
//            allPassingMutants[sourceFilePath] = passingMutants
//        }
//    }
//    printIsolationGlobalStatistics { logger.debug(it) }
//    File(outputPath.substringBeforeLast('/')).mkdirs()
//    File(outputPath).printWriter().use { writer ->
//        writer.println("Current coverage: ${CompilerInstrumentation.coverageType}")
//        writer.println()
//        for ((path, comparisonsList) in comparisons) {
//            writer.println(path)
//            writer.println("Numbers of failing mutants: ${allFailingMutants[path]}")
//            writer.println("Average number of failing mutants: ${allFailingMutants[path]!!.average()}")
//            writer.println("Numbers of passing mutants: ${allPassingMutants[path]}")
//            writer.println("Average number of passing mutants: ${allPassingMutants[path]!!.average()}")
//            writer.println("Mean similarity value: ${comparisonsList.average()}")
//            writer.println("Min similarity value: ${comparisonsList.min()}")
//            writer.println("Max similarity value: ${comparisonsList.max()}")
//            writer.println("Similarity value's range: ${comparisonsList.max()!! - comparisonsList.min()!!}")
//            writer.println()
//        }
//    }
//    logger.debug("")
//    logger.debug("files with no bugs:")
//    for (fileWithNoBug in filesWithNoBugs) {
//        logger.debug(fileWithNoBug)
//    }
//    logger.debug("")
//    logger.debug("fucked up files:")
//    for (fuckedUpFile in fuckedUpFiles) {
//        logger.debug(fuckedUpFile)
//    }
//}

fun filterSameSamples(filePath: String) {
    val allSamples = mutableSetOf<Pair<Sample, String>>()
    val samplesByGroup = mutableMapOf<String, MutableSet<Pair<String, String>>>()
    val filteredSamples = mutableListOf<Pair<Sample, Sample>>()
    val repeatedSamples = mutableListOf<Pair<Sample, Sample>>()

    File(filePath)
        .walk().sortedBy { it.absolutePath }
        .forEach {
            val sourceFilePath = it.absolutePath
            regex.find(sourceFilePath)?.let { matchResult ->
                val sampleID = matchResult.groupValues
                val sample = Sample(sampleID[1], sampleID[2])
                val code = it.readLines().joinToString("\n").replace(Regex("\\s"), "")
                if (sample.group in samplesByGroup) {
                    val firstSearch = samplesByGroup[sample.group]!!.find { s -> s.second == code }
                    if (firstSearch != null) {
                        filteredSamples += Sample(sample.group, firstSearch.first) to sample
                        return@forEach
                    }
                    val secondSearch = allSamples.find { s -> s.second == code }
                    if (secondSearch != null) {
                        repeatedSamples += secondSearch.first to sample
                        return@forEach
                    }
                    samplesByGroup[sample.group]!! += sample.id to code
                    allSamples += sample to code
                } else {
                    val secondSearch = allSamples.find { s -> s.second == code }
                    if (secondSearch != null) {
                        repeatedSamples += secondSearch.first to sample
                        return@forEach
                    }
                    samplesByGroup[sample.group] = mutableSetOf(sample.id to code)
                    allSamples += sample to code
                }
            }
        }

    logger.debug("filteredSamples (${filteredSamples.size} items):\n${filteredSamples.joinToString("\n")}\n\n")
    logger.debug("repeatedSamples (${repeatedSamples.size} items):\n${repeatedSamples.joinToString("\n")}\n\n")
}

fun filterBadSamples(filePath: String) {
    val fineSamples = mutableListOf<String>()
    val badSamples = mutableListOf<String>()
    val terribleSamples = mutableListOf<String>()

    File(filePath).walk().sortedBy { it.absolutePath }.forEach { sourceFile ->
        val sourceFilePath = sourceFile.absolutePath
        val matchResult = regex.find(sourceFilePath)
        if (matchResult != null) {
            fun debug(e: Throwable) {
                logger.debug("@ $sourceFilePath")
                logger.debug("Exception: ${e.javaClass.name}")
                logger.debug(if (e.message.isNullOrEmpty()) "no message" else e.message)
                logger.debug("")
                terribleSamples += sourceFilePath
            }

            try {
                val creator = PSICreator("")
                val file = creator.getPSIForFile(sourceFilePath)
                Transformation.file = file
                WitnessTestsCollector(getCompiler())
                fineSamples += sourceFilePath
            } catch (e: NoBugFoundException) {
                logger.debug("$sourceFilePath does not contain a detectable bug!")
                logger.debug("")
                badSamples += sourceFilePath
            } catch (e: Throwable) {
                debug(e)
            }
        }
    }

    logger.debug("Fine samples (${fineSamples.size} items):\n${fineSamples.joinToString("\n")}\n\n")
    logger.debug("Bad samples (${badSamples.size} items):\n${badSamples.joinToString("\n")}\n\n")
    logger.debug("Terrible samples (${terribleSamples.size} items):\n${terribleSamples.joinToString("\n")}\n\n")
}

//fun numerateDataSet(file: File) {
//    var counter = 0
//    val files = file.listFiles() ?: return
//    files.sortedBy { it.absolutePath }.forEach { childFile ->
//        if (childFile.isDirectory) {
//            numerateDataSet(childFile)
//        } else {
//            val content = childFile.readLines().joinToString("\n")
//            childFile.delete()
//            File("${file.absolutePath}/$counter.kt").writeText(content)
//            counter++
//        }
//    }
//}
