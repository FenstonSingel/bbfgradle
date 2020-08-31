package com.stepanov.bbf

import com.stepanov.bbf.bugfinder.isolation.BugIsolator
import com.stepanov.bbf.bugfinder.isolation.RankedProgramEntities
import com.stepanov.bbf.bugfinder.isolation.formulas.*
import com.stepanov.bbf.bugfinder.manager.BugType
import com.stepanov.bbf.coverage.CompilerInstrumentation
import org.apache.log4j.Logger
import org.apache.log4j.PropertyConfigurator
import java.io.File
import kotlin.collections.*

val logger: Logger = Logger.getLogger("isolationTestbedLogger")

fun statistics() {
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
    logger.debug("")
}

val coverageType = "${CompilerInstrumentation.coverageType}"
const val set = "set-a-1"
const val tag = "$set-isolationResult-0"

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
    compareRankings(
            File("/home/fenstonsingel/kotlin-samples/$set/"),
            "/home/fenstonsingel/isolation-stats/comparisons/$coverageType/$tag.log"
    )
    //checkStability(File("/home/fenstonsingel/kotlin-samples/stability-tests/"), 10, 10)

}

data class Sample(val group: Int, val number: Int) {
    override fun toString(): String = "$group/$number"
}

fun compareRankings(file: File, outputPath: String) {
    val regex = Regex("""(\d+)/([^/]+)\.kt${'$'}""")
    val rankings = mutableMapOf<Sample, RankedProgramEntities>()
    file.walk().sortedBy { it.absolutePath }.forEach {
        val sourceFilePath = it.absolutePath
        val matchResult = regex.find(sourceFilePath)
        if (matchResult != null) {
            val ids = matchResult.groupValues
            val sample = Sample(ids[1].toInt(), ids[2].toInt())
            val ranking = BugIsolator.isolate(sourceFilePath, BugType.BACKEND)
            isolationStats("$sample", ranking)
            logger.debug("finished isolating $sourceFilePath")
            logger.debug("")
            statistics()
            rankings[sample] = ranking
        }
    }
    statistics()
    val comparisons = mutableListOf<Triple<Sample, Sample, Double>>()
    for ((sample1, ranking1) in rankings) {
        for ((sample2, ranking2) in rankings) {
            if (sample1 != sample2 && comparisons.find { (s1, s2, _) -> s1 == sample2 && s2 == sample1 } == null) {
                comparisons += Triple(sample1, sample2, ranking1.cosineSimilarity(ranking2))
            }
        }
    }
    File(outputPath.substringBeforeLast('/')).mkdirs()
    File(outputPath).printWriter().use { writer ->
        writer.println("Current coverage: ${CompilerInstrumentation.coverageType}")
        for (comparison in comparisons.sortedByDescending { (_, _, ranking) -> ranking }) {
            val sample1 = comparison.first
            val sample2 = comparison.second
            writer.println("$sample1 to $sample2: ${comparison.third} ${if (sample1.group == sample2.group) "(DUPLICATES)" else ""}")
        }
    }
}

// TODO update file management
fun checkStability(file: File, numberOfSamples: Int, numberOfIterations: Int) {
    val regex = Regex("""/([^/]+)\.kt${'$'}""")
    val comparisons = mutableMapOf<String, List<Double>>()
    file.walk().filter { it.isFile }.toList().shuffled().take(numberOfSamples).forEach { sample ->
        val sourceFilePath = sample.absolutePath
        val matchResult = regex.find(sourceFilePath)
        if (matchResult != null) {
            val rankings = mutableListOf<RankedProgramEntities>()
            for (x in 0 until numberOfIterations) {
                rankings += BugIsolator.isolate(sourceFilePath, BugType.BACKEND)
                logger.debug("finished isolating $sourceFilePath")
                logger.debug("")
                statistics()
            }
            val localComparisons = mutableListOf<Double>()
            for (i in 0 until rankings.size) {
                for (j in i + 1 until rankings.size) {
                    localComparisons += rankings[i].cosineSimilarity(rankings[j])
                }
            }
            comparisons[sourceFilePath] = localComparisons
        }
    }
    statistics()
    println("Current coverage: ${CompilerInstrumentation.coverageType}")
    println()
    for ((path, comparisonsList) in comparisons) {
        println(path)
        println("Mean similarity value: ${comparisonsList.average()}")
        println("Min similarity value: ${comparisonsList.min()}")
        println("Max similarity value: ${comparisonsList.max()}")
        println("Similarity value's range: ${comparisonsList.max()!! - comparisonsList.min()!!}")
        println()
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

fun initialTests() {
    BugIsolator.rankingFormula = Ochiai2RankingFormula

    val ranking1 = BugIsolator.isolate("/home/fenstonsingel/kotlin-samples/set-a/3/BACKEND_bhugqgy_FILE.kt", BugType.BACKEND)
    statistics()

    val ranking2 = BugIsolator.isolate("/home/fenstonsingel/kotlin-samples/set-a/6/BACKEND_dooqtxk_FILE.kt", BugType.BACKEND)
    statistics()

    logger.debug(ranking1.cosineSimilarity(ranking2))
    logger.debug("")
    logger.debug("")

    val ranking3 = BugIsolator.isolate("/home/fenstonsingel/kotlin-samples/set-a/3/BACKEND_pytmh.kt", BugType.BACKEND)
    statistics()

    logger.debug(ranking1.cosineSimilarity(ranking3))
    logger.debug("")
    logger.debug("")
}
