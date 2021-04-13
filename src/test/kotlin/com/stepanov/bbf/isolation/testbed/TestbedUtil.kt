package com.stepanov.bbf.isolation.testbed

import com.stepanov.bbf.bugfinder.Reducer
import com.stepanov.bbf.bugfinder.executor.Checker
import com.stepanov.bbf.bugfinder.isolation.*
import com.stepanov.bbf.bugfinder.manager.BugType
import com.stepanov.bbf.bugfinder.mutator.transformations.Transformation
import com.stepanov.bbf.reduktor.parser.PSICreator
import kotlinx.serialization.Serializable
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.list
import org.apache.log4j.Logger
import org.jetbrains.kotlin.psi.KtFile
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

val isolationTestbedLogger: Logger = Logger.getLogger("isolationTestbedLogger")

val kotlinSampleRegex = Regex("""([^/]+)/([^/]+)\.kt${'$'}""")

const val utilRandomSeed = 56239485L

fun reinitializeRandom(seed: Long) = com.stepanov.bbf.bugfinder.util.reinitializeRandom(seed)

fun filterSamplesByIdentity(datasetPath: String) {
    val allSamples = mutableSetOf<String>()
    val sampleToID = mutableMapOf<String, String>()
    val identicalSamples = mutableListOf<Pair<String, String>>()

    File(datasetPath).walk().sortedBy { it.absolutePath }.forEach { sourceFile ->
        val sourceFilePath = sourceFile.absolutePath
        kotlinSampleRegex.find(sourceFilePath)?.let { matchResult ->
            val id = matchResult.value
            val code = sourceFile.readLines().joinToString("") { str -> str
                .replace(Regex("//.*$"), "")
                .replace(Regex("\\s"), "")
            }
            isolationTestbedLogger.debug("Next sample after cleaning up:\n$code")
            if (code in allSamples) {
                identicalSamples.add(id to sampleToID[code]!!)
            } else {
                allSamples += code
                sampleToID[code] = id
            }
        }
    }

    isolationTestbedLogger.debug("Identical samples (${identicalSamples.size} items):\n${identicalSamples.joinToString("\n")}\n\n")
}

val defaultBugInfo = BugInfo(BugType.BACKEND, listOf("JVM" to ""))

fun filterSamplesByBugPresence(datasetPath: String, defaultBugInfo: BugInfo) {
    val fineSamples = mutableListOf<String>()
    val samplesWithNoBugs = mutableListOf<String>()
    val erroneousSamples = mutableListOf<String>()

    File(datasetPath).walk().sortedBy { it.absolutePath }.forEach { sourceFile ->
        val sourceFilePath = sourceFile.absolutePath
        kotlinSampleRegex.find(sourceFilePath)?.let { _ ->
            fun debug(e: Throwable) {
                isolationTestbedLogger.debug("@ $sourceFilePath")
                isolationTestbedLogger.debug("Exception: ${e.javaClass.name}")
                isolationTestbedLogger.debug(if (e.message.isNullOrEmpty()) "no message" else e.message)
                isolationTestbedLogger.debug("")
            }

            try {
                PSICreator("").getPSIForFile(sourceFilePath)
                val checkResult = BugIsolator.constructChecker(defaultBugInfo, filterInvalidCode = true).checkTest(
                    sourceFile.readText(), "tmp/tmp.kt"
                )
                if (checkResult) fineSamples += sourceFilePath
                else samplesWithNoBugs += sourceFilePath
            } catch (e: Throwable) {
                debug(e)
                erroneousSamples += sourceFilePath
            }
        }
    }

    isolationTestbedLogger.debug("Fine samples (${fineSamples.size} items):\n${fineSamples.joinToString("\n")}\n\n")
    isolationTestbedLogger.debug("Samples with no bugs (${samplesWithNoBugs.size} items):\n${samplesWithNoBugs.joinToString("\n")}\n\n")
    isolationTestbedLogger.debug("Erroneous samples (${erroneousSamples.size} items):\n${erroneousSamples.joinToString("\n")}\n\n")
}

fun reduceAllSamplesInDataset(datasetPath: String, defaultBugInfo: BugInfo) {
    val compiler = defaultBugInfo.firstCompiler
    val checker = BugIsolator.constructChecker(defaultBugInfo, filterInvalidCode = true)
    File(datasetPath).walk().sortedBy { it.absolutePath }.forEach {
        val sourceFilePath = it.absolutePath
        kotlinSampleRegex.find(sourceFilePath)?.let { _ ->
            File("tmp/tmp.kt").writeText(File(sourceFilePath).readText())
            try {
                File("tmp/reduce_tmp.kt").writeText(File(sourceFilePath).readText())
                val executor = Executors.newSingleThreadExecutor()
                isolationTestbedLogger.debug("started reducing $sourceFilePath")
                val future = executor.submit {
                    Reducer.reduce("tmp/reduce_tmp.kt", compiler)
                }
                try {
                    future.get(20, TimeUnit.MINUTES)
                } catch (e: TimeoutException) {
                    isolationTestbedLogger.debug("timeout!!")
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
                isolationTestbedLogger.debug("finished reducing $sourceFilePath")
            } catch (e: Throwable) {
                isolationTestbedLogger.debug("Exception: ${e.javaClass.name}")
                isolationTestbedLogger.debug(if (e.message.isNullOrEmpty()) "no message" else e.message)
                isolationTestbedLogger.debug("$sourceFilePath not fully reduced")
            } finally {
                val reducedText = File("tmp/reduce_tmp.kt").readText()
                if (checker.checkTest(reducedText, "tmp/reduce_tmp.kt")) {
                    isolationTestbedLogger.debug("bug was preserved during reduction => saving reduced version")
                    File("tmp/tmp.kt").writeText(reducedText)
                } else {
                    isolationTestbedLogger.debug("bug was not preserved during reduction => back to original version")
                }
                isolationTestbedLogger.debug("")
            }
            val newSampleFilePath = sourceFilePath.replace(datasetPath, "$datasetPath-reduced")
            File(newSampleFilePath.substringBeforeLast("/")).mkdirs()
            File(newSampleFilePath).writeText(File("tmp/tmp.kt").readText())
        }
    }
}

@Serializable
data class Sample(val group: String, val id: String) {
    fun joinToString(separator: String) = "$group$separator$id"
    override fun toString(): String = joinToString("::")
}

@Serializable
class SampleComparison(val first: Sample, val second: Sample, val similarity: Double) {
    val areDuplicates = first.group == second.group
    override fun toString(): String = "$first to $second: $similarity"
}

fun <T> estimateSimilaritiesForSamplesInDataset(
    datasetPath: String,
    estimationTag: String,
    evaluateDataForSample: (String, Sample) -> T?,
    estimateSamplesSimilarities: (T, T) -> Double
) {
    val unevaluatedSamples = mutableListOf<String>()

    val allEvaluationResults = mutableListOf<Pair<Sample, T>>()
    val sampleComparisons = mutableListOf<SampleComparison>()

    File(datasetPath).walk().sortedBy { it.absolutePath }.forEach { sourceFile ->
        val sourceFilePath = sourceFile.absolutePath
        kotlinSampleRegex.find(sourceFilePath)?.let { matchResult ->
            val sampleID = matchResult.groupValues
            val sample = Sample(sampleID[1], sampleID[2])

            File("tmp/tmp.kt").writeText(File(sourceFilePath).readText())

            val evaluationResult: T? = try {
                evaluateDataForSample(sourceFilePath, sample)
            } catch (e: Exception) {
                isolationTestbedLogger.debug(e)
                isolationTestbedLogger.debug("")
                null
            }

            evaluationResult?.also {
                allEvaluationResults.forEach { (oldSample, oldEvaluationResult) ->
                    val similarity = estimateSamplesSimilarities(evaluationResult, oldEvaluationResult)
                    sampleComparisons += SampleComparison(sample, oldSample, similarity)
                }
                allEvaluationResults += sample to evaluationResult
            }
            if (evaluationResult == null) unevaluatedSamples += sample.toString()
        }
    }

    sampleComparisons.sortByDescending { sampleComparison -> sampleComparison.similarity }

    val resultsDirPath = datasetPath.replaceFirst("samples", "results")

    File(resultsDirPath).mkdirs()

    val serializedSampleComparisons = Cbor.dump(SampleComparison.serializer().list, sampleComparisons)
    File("$resultsDirPath/$estimationTag.cbor").writeBytes(serializedSampleComparisons)

    File("$resultsDirPath/$estimationTag.log").printWriter().use { writer ->
        for (comparison in sampleComparisons) {
            writer.print(comparison)
            if (comparison.areDuplicates) writer.print(" (duplicates)")
            writer.println()
        }
        writer.println()
    }

    if (unevaluatedSamples.isNotEmpty()) {
        isolationTestbedLogger.debug("following samples were not evaluated: $unevaluatedSamples")
    } else {
        isolationTestbedLogger.debug("all samples have been evaluated!")
    }
}

fun loadEstimationResults(filePath: String): List<SampleComparison> =
    Cbor.load(SampleComparison.serializer().list, File(filePath).readBytes())

data class ThresholdPerformance(
    val threshold: Double,
    val truePositives: Int,
    val falsePositives: Int,
    val trueNegatives: Int,
    val falseNegatives: Int,
    val defaultBeta: Double = 1.0
) {
    val precision get() = truePositives.toDouble() / (truePositives + falsePositives)

    val recall get() = truePositives.toDouble() / (truePositives + falseNegatives)

    fun calculateFScore(beta: Double = defaultBeta): Double {
        val numerator = (1 + beta * beta) * truePositives
        val denominator = numerator + beta * beta * falseNegatives + falsePositives
        return numerator / denominator
    }

    override fun toString(): String =
        "P: %.2f%%, R: %.2f%%, F_$defaultBeta: %.2f%%".format(
            precision * 100, recall * 100, calculateFScore() * 100
        )
}

fun List<SampleComparison>.calculateAllPossibleFScores(beta: Double): List<ThresholdPerformance> {
    val numberOfDuplicatePairs = count { sampleComparison -> sampleComparison.areDuplicates }
    val numberOfDifferentPairs = size - numberOfDuplicatePairs

    var truePositives = 0
    var falsePositives = 0
    var trueNegatives = numberOfDifferentPairs
    var falseNegatives = numberOfDuplicatePairs

    val performances = mutableListOf<ThresholdPerformance>()
    var lastThreshold: Double? = null
    for (sampleComparison in this) {
        if (lastThreshold != sampleComparison.similarity) {
            lastThreshold = sampleComparison.similarity
            performances += ThresholdPerformance(
                lastThreshold,
                truePositives, falsePositives, trueNegatives, falseNegatives,
                beta
            )
        }
        if (sampleComparison.areDuplicates) {
            truePositives += 1
            falseNegatives -= 1
        } else {
            falsePositives += 1
            trueNegatives -= 1
        }
    }

    return performances.sortedByDescending { performance -> performance.calculateFScore() }
}

class MutantGenerator private constructor(
    private val mutations: List<Transformation>,
    private val serializationDirPath: String,
    private val mutantsExportTag: String
) : Checker() {

    private fun generate(
        sampleFilePath: String, serializationTag: String
    ) {
        isolationTestbedLogger.debug("started mutating $sampleFilePath")

        // sometimes PSICreator trips up badly and there's nothing we can do about it
        val initialFile: KtFile
        try {
            initialFile = psiCreator.getPSIForFile(sampleFilePath)
        } catch (e: Throwable) {
            throw PSICreatorException(e)
        }

        // setting up the Transformation environment
        // the checker ref should not change throughout the entire mutation process
        Transformation.file = initialFile
        Transformation.checker = this

        // setting up this class's environment
        mutantsCatalog = mutableSetOf()

        // generating mutants (duh)
        for (mutation in mutations) {
            try {
                mutation.transform()
            } catch (e: Throwable) {
                // if something bad happens when we mutate, we just
                // halt a particular mutation and turn to the next one
                isolationTestbedLogger.debug(e.message)
            }
        }

        // serializing mutants for later use
        File("$serializationDirPath/$serializationTag").mkdirs()
        MutantsForIsolation(mutantsExportTag, initialFile.text, mutantsCatalog.toList()).export(
            "$serializationDirPath/$serializationTag/mutants-$mutantsExportTag.cbor"
        )

        isolationTestbedLogger.debug("finished mutating $sampleFilePath")
        isolationTestbedLogger.debug("")
    }

    override fun checkCompiling(file: KtFile): Boolean = checkTextCompiling(file.text)

    override fun checkTextCompiling(text: String): Boolean {
        mutantsCatalog.add(text)
        return false // keeping original sample mutating
    }

    private val psiCreator = PSICreator("")

    // a collection of all interesting mutants in case we want to serialize them
    private lateinit var mutantsCatalog: MutableSet<String>

    companion object {
        fun generate(
            datasetDirPath: String,
            mutations: List<Transformation>,
            mutantsExportTag: String
        ) {
            val generator = MutantGenerator(
                mutations, datasetDirPath.replace("samples", "serialization"), mutantsExportTag
            )

            File(datasetDirPath).walk().sortedBy { it.absolutePath }.forEach { sourceFile ->
                val sourceFilePath = sourceFile.absolutePath
                kotlinSampleRegex.find(sourceFilePath)?.let { matchResult ->
                    val sampleID = matchResult.groupValues
                    val sample = Sample(sampleID[1], sampleID[2])

                    generator.generate(sourceFilePath, serializationTag = sample.joinToString("/"))
                }
            }

            isolationTestbedLogger.debug("mutations done!")
        }
    }

}
