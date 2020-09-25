package com.stepanov.bbf

import com.stepanov.bbf.bugfinder.isolation.BugIsolator
import com.stepanov.bbf.bugfinder.isolation.RankedProgramEntities
import com.stepanov.bbf.bugfinder.manager.BugType
import com.stepanov.bbf.coverage.CompilerInstrumentation
import org.apache.log4j.Logger
import org.apache.log4j.PropertyConfigurator
import java.io.File
import kotlin.collections.*

val logger: Logger = Logger.getLogger("isolationTestbedLogger")

fun statistics() {
    logger.debug("")
    logger.debug("Isolations: ${BugIsolator.numberOfIsolations}")
    logger.debug("Total isolation time: ${BugIsolator.totalIsolationTime}")
    logger.debug("Average isolation time: ${BugIsolator.meanIsolationTime}")
    logger.debug("Isolation time's standard deviation: ${BugIsolator.isolationTimeSD}")
    logger.debug("Minimum isolation time: ${BugIsolator.minIsolationTime}")
    logger.debug("Maximum isolation time: ${BugIsolator.maxIsolationTime}")
    logger.debug("")
    logger.debug("Compilations: ${BugIsolator.numberOfCompilations}")
    logger.debug("Time spent on instrumentation: ${CompilerInstrumentation.timeSpentOnInstrumentation}")
    logger.debug("Total coverage recording time: ${BugIsolator.totalInstrPerformanceTime}")
    logger.debug("Average coverage recording time: ${BugIsolator.meanInstrPerformanceTime}")
    logger.debug("")
    logger.debug("Total failing code samples generated: ${BugIsolator.totalFailingCodeSamples}")
    logger.debug("Average failing code samples generated: ${BugIsolator.meanFailingCodeSamples}")
    logger.debug("Total passing code samples generated: ${BugIsolator.totalPassingCodeSamples}")
    logger.debug("Average passing code samples generated: ${BugIsolator.meanPassingCodeSamples}")
    logger.debug("")
    logger.debug("Distribution of samples per mutation:")
    BugIsolator.codeSampleDistributionPerMutation.toSortedMap().forEach { key, (bugsRatio, successesRatio) ->
        logger.debug("   $key: %.2f%% fails and %.2f%% passes".format(bugsRatio * 100, successesRatio * 100))
    }
    logger.debug("Quality of samples per mutation:")
    BugIsolator.codeSampleQualityPerMutation.toSortedMap().forEach { key, (bugsAvgDist, successesAvgDist) ->
        logger.debug("   $key: $bugsAvgDist avg. fail cosDist and $successesAvgDist avg. pass cosDist")
    }
    logger.debug("")
}

val coverageType = "${CompilerInstrumentation.coverageType}"
const val set = "ground-truth"
const val tag = "$set-evaluation"

const val rankingsPath = "/home/fenstonsingel/isolation-stats/results"

fun isolationStats(name: String, ranking: RankedProgramEntities) {
    val fileName = "$rankingsPath/${CompilerInstrumentation.coverageType}/$tag/$name.log"
    File(fileName.substringBeforeLast('/')).mkdirs()
    File(fileName).printWriter().use { writer ->
        writer.println("failing mutants: ${BugIsolator.lastNumberOfFailingMutants}")
        writer.println("passing mutants: ${BugIsolator.lastNumberOfPassingMutants}")
        writer.println()
        for ((entity, rank) in ranking.toList()) {
            writer.println("$entity - $rank")
        }
    }
}

fun main() {

    PropertyConfigurator.configure("src/main/resources/bbfLog4j.properties")
    PropertyConfigurator.configure("src/main/resources/reduktorLog4j.properties")

    println("Current coverage: ${CompilerInstrumentation.coverageType}")

//    numerateDataSet(File("/home/fenstonsingel/kotlin-samples/$set/"))

    compareRankings(
        File("/home/fenstonsingel/kotlin-samples/$set/"),
        "/home/fenstonsingel/isolation-stats/comparisons/$coverageType/$tag.log",
        false
    )

//    checkStability(
//        File("/home/fenstonsingel/kotlin-samples/$set/"),
//        75, 3,
//        "/home/fenstonsingel/isolation-stats/comparisons/$coverageType/$tag.log"
//    )

}

data class Sample(val group: String, val number: Int) {
    override fun toString(): String = "$group/$number"
}

fun compareRankings(file: File, outputPath: String, isKendall: Boolean = false) {
    val regex = Regex("""([^/]+)/([^/]+)\.kt${'$'}""")
    val rankings = mutableMapOf<Sample, RankedProgramEntities>()
    val filesWithNoBugs = mutableListOf<String>()
    val fuckedUpFiles = mutableListOf<String>()
    file.walk().sortedBy { it.absolutePath }.forEach {
        val sourceFilePath = it.absolutePath
        val matchResult = regex.find(sourceFilePath)
        if (matchResult != null) {
            logger.debug("started isolating $sourceFilePath")
            try {
                val ids = matchResult.groupValues
                val sample = Sample(ids[1], ids[2].toInt())
                val ranking = BugIsolator.isolate(sourceFilePath, BugType.BACKEND)
                if (ranking != null) {
                    isolationStats("$sample", ranking)
                    statistics()
                    rankings[sample] = ranking
                } else {
                    filesWithNoBugs += sourceFilePath
                }
            } catch (e: Exception) {
                logger.debug(e.message)
                fuckedUpFiles += sourceFilePath
            }
            logger.debug("finished isolating $sourceFilePath")
            logger.debug("")
        }
    }
    statistics()
    val comparisons = mutableListOf<Triple<Sample, Sample, Double>>()
    for ((sample1, ranking1) in rankings) {
        for ((sample2, ranking2) in rankings) {
            if (sample1 != sample2 && comparisons.find { (s1, s2, _) -> s1 == sample2 && s2 == sample1 } == null) {
                comparisons += Triple(
                    sample1, sample2,
                    if (isKendall) ranking1.kendallTauDistance(ranking2).toDouble() else ranking1.cosineSimilarity(ranking2)
                )
            }
        }
    }
    var uniquesComparisons = 0
    var duplicatesComparisons = 0
    var uniquesDivergence = 0.0
    var duplicatesDivergence = 0.0
    val threshold = 0.8
    var uniquesDivRelativeToT = 0
    var duplicatesDivRelativeToT = 0
    var firstUniques = -1
    var firstUniquesValue = 0.0
    var lastDuplicates = -1
    var lastDuplicatesValue = 0.0
    File(outputPath.substringBeforeLast('/')).mkdirs()
    File(outputPath).printWriter().use { writer ->
        var index = 1
        for ((sample1, sample2, similarity) in comparisons.sortedByDescending { (_, _, ranking) -> ranking }) {
            writer.print("$sample1 to $sample2: $similarity ")
            if (sample1.group == sample2.group) {
                writer.println("(DUPLICATES)")
                ++duplicatesComparisons
                lastDuplicates = index
                lastDuplicatesValue = similarity
                duplicatesDivergence += 1 - similarity
                if (similarity <= threshold) ++duplicatesDivRelativeToT
            } else {
                writer.println()
                ++uniquesComparisons
                if (firstUniques == -1) {
                    firstUniques = index
                    firstUniquesValue = similarity
                }
                uniquesDivergence += similarity
                if (similarity > threshold) ++uniquesDivRelativeToT
            }
            ++index
        }
        writer.println()
        writer.println("Current coverage: ${CompilerInstrumentation.coverageType}")
        writer.println("Worst-ranked duplicates: $lastDuplicates")
        writer.println("(value: $lastDuplicatesValue)")
        writer.println("Worst-ranked uniques: $firstUniques")
        writer.println("(value: $firstUniquesValue)")
        writer.println("Range of mixed entries: ${lastDuplicates - firstUniques}")
        writer.println("Absolute duplicates divergence: $duplicatesDivergence")
        writer.println("(normalized: ${duplicatesDivergence / duplicatesComparisons})")
        writer.println("Absolute uniques divergence: $uniquesDivergence")
        writer.println("(normalized: ${uniquesDivergence / uniquesComparisons})")
        writer.println("Duplicates divergence relative to $threshold: $duplicatesDivRelativeToT")
        writer.println("(normalized: ${duplicatesDivRelativeToT.toDouble() / duplicatesComparisons})")
        writer.println("Uniques divergence relative to $threshold: $uniquesDivRelativeToT")
        writer.println("(normalized: ${uniquesDivRelativeToT.toDouble() / uniquesComparisons})")
        writer.println("Total divergence: ${duplicatesDivergence + uniquesDivergence}")
        writer.println("(normalized: ${(duplicatesDivergence + uniquesDivergence) / (duplicatesComparisons + uniquesComparisons)})")
    }
    logger.debug("")
    logger.debug("files with no bugs:")
    for (fileWithNoBug in filesWithNoBugs) {
        logger.debug(fileWithNoBug)
    }
    logger.debug("")
    logger.debug("fucked up files:")
    for (fuckedUpFile in fuckedUpFiles) {
        logger.debug(fuckedUpFile)
    }
}

fun checkStability(file: File, numberOfSamples: Int, numberOfIterations: Int, outputPath: String) {
    val regex = Regex("""/([^/]+)\.kt${'$'}""")
    val comparisons = mutableMapOf<String, List<Double>>()
    val allFailingMutants = mutableMapOf<String, List<Int>>()
    val allPassingMutants = mutableMapOf<String, List<Int>>()
    val filesWithNoBugs = mutableListOf<String>()
    val fuckedUpFiles = mutableListOf<String>()
    file.walk().filter { it.isFile }.toList().shuffled().take(numberOfSamples).forEach { sample ->
        val sourceFilePath = sample.absolutePath
        val matchResult = regex.find(sourceFilePath)
        if (matchResult != null) {
            val rankings = mutableListOf<RankedProgramEntities>()
            val failingMutants = mutableListOf<Int>()
            val passingMutants = mutableListOf<Int>()
            for (x in 0 until numberOfIterations) {
                logger.debug("started isolating $sourceFilePath")
                try {
                    val ranking = BugIsolator.isolate(sourceFilePath, BugType.BACKEND)
                    if (ranking != null) {
                        isolationStats("${sample.parentFile.name}/${sample.nameWithoutExtension}/$x", ranking)
                        statistics()
                        rankings += ranking
                        failingMutants += BugIsolator.lastNumberOfFailingMutants
                        passingMutants += BugIsolator.lastNumberOfPassingMutants
                    } else {
                        filesWithNoBugs += sourceFilePath
                    }
                } catch (e: Exception) {
                    logger.debug(e.message)
                    fuckedUpFiles += sourceFilePath
                }
                logger.debug("finished isolating $sourceFilePath")
                logger.debug("")
            }
            val localComparisons = mutableListOf<Double>()
            for (i in 0 until rankings.size) {
                for (j in i + 1 until rankings.size) {
                    localComparisons += rankings[i].cosineSimilarity(rankings[j])
                }
            }
            comparisons[sourceFilePath] = localComparisons
            allFailingMutants[sourceFilePath] = failingMutants
            allPassingMutants[sourceFilePath] = passingMutants
        }
    }
    statistics()
    File(outputPath.substringBeforeLast('/')).mkdirs()
    File(outputPath).printWriter().use { writer ->
        writer.println("Current coverage: ${CompilerInstrumentation.coverageType}")
        writer.println()
        for ((path, comparisonsList) in comparisons) {
            writer.println(path)
            writer.println("Numbers of failing mutants: ${allFailingMutants[path]}")
            writer.println("Average number of failing mutants: ${allFailingMutants[path]!!.average()}")
            writer.println("Numbers of passing mutants: ${allPassingMutants[path]}")
            writer.println("Average number of passing mutants: ${allPassingMutants[path]!!.average()}")
            writer.println("Mean similarity value: ${comparisonsList.average()}")
            writer.println("Min similarity value: ${comparisonsList.min()}")
            writer.println("Max similarity value: ${comparisonsList.max()}")
            writer.println("Similarity value's range: ${comparisonsList.max()!! - comparisonsList.min()!!}")
            writer.println()
        }
    }
    logger.debug("")
    logger.debug("files with no bugs:")
    for (fileWithNoBug in filesWithNoBugs) {
        logger.debug(fileWithNoBug)
    }
    logger.debug("")
    logger.debug("fucked up files:")
    for (fuckedUpFile in fuckedUpFiles) {
        logger.debug(fuckedUpFile)
    }
}

fun filterBadSamples(file: File) {
    val regex = Regex("""(\d+/[^/]+)\.kt$""")
    file.walk().sortedBy { it.absolutePath }.forEach {
        val sourceFilePath = it.absolutePath
        val matchResult = regex.find(sourceFilePath)
        if (matchResult != null) {
            try {
                BugIsolator.isolate(sourceFilePath, BugType.BACKEND)
                statistics()
            } catch (e: IllegalArgumentException) {
                if (e.message == "A project should contain a bug in order to isolate it.") {
                    logger.debug("$sourceFilePath does not contain a detectable bug!")
                    logger.debug("")
                    logger.debug("")
                }
            }
        }
    }
    statistics()
}

fun numerateDataSet(file: File) {
    var counter = 0
    val files = file.listFiles() ?: return
    files.sortedBy { it.absolutePath }.forEach { childFile ->
        if (childFile.isDirectory) {
            numerateDataSet(childFile)
        } else {
            val content = childFile.readLines().joinToString("\n")
            childFile.delete()
            File("${file.absolutePath}/$counter.kt").writeText(content)
            counter++
        }
    }
}

//fun initialTests() {
//    BugIsolator.rankingFormula = Ochiai2RankingFormula
//
//    val ranking1 = BugIsolator.isolate("/home/fenstonsingel/kotlin-samples/set-a/3/BACKEND_bhugqgy_FILE.kt", BugType.BACKEND)
//    statistics()
//
//    val ranking2 = BugIsolator.isolate("/home/fenstonsingel/kotlin-samples/set-a/6/BACKEND_dooqtxk_FILE.kt", BugType.BACKEND)
//    statistics()
//
//    logger.debug(ranking1.cosineSimilarity(ranking2))
//    logger.debug("")
//    logger.debug("")
//
//    val ranking3 = BugIsolator.isolate("/home/fenstonsingel/kotlin-samples/set-a/3/BACKEND_pytmh.kt", BugType.BACKEND)
//    statistics()
//
//    logger.debug(ranking1.cosineSimilarity(ranking3))
//    logger.debug("")
//    logger.debug("")
//}
