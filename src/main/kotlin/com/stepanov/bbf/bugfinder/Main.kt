package com.stepanov.bbf.bugfinder

import com.stepanov.bbf.bugfinder.isolation.BugIsolator
import com.stepanov.bbf.bugfinder.isolation.CollapsedMutationStatistics
import com.stepanov.bbf.bugfinder.isolation.MutationStatistics
import com.stepanov.bbf.bugfinder.manager.BugType
import com.stepanov.bbf.coverage.analysis.Ochiai2RankingFormula
import com.stepanov.bbf.coverage.analysis.OchiaiRankingFormula
import com.stepanov.bbf.coverage.analysis.RankedEntityList
import com.stepanov.bbf.coverage.data.EntityStatisticsSet
import com.stepanov.bbf.coverage.extraction.CoverageComposer
import com.stepanov.bbf.coverage.util.*
import org.apache.log4j.PropertyConfigurator
import java.io.File

fun main(args: Array<String>) {

    PropertyConfigurator.configure("src/main/resources/bbfLog4j.properties")
    PropertyConfigurator.configure("src/main/resources/reduktorLog4j.properties")

    /*
    val compiler = JVMCompiler()
    val execData = compiler.getExecutionData("/home/ruban/kotlin-samples/duplicates-set-s/group1duplicate0.kt")
    val coverage = CoverageComposer.composeFrom(execData, EntityType.LINES, SegmentType.LINES)
    coverage.removeEmptyEntries()
    System.exit(0)
    */

    /*
    val commonLines = File("tmp/commonLines.json")
    val text = commonLines.readText().replace("\"entityType\":\"LINES\",\"segmentType\":\"LINES\",", "")
    commonLines.writeText(text)
    System.exit(0)
    */

    /*
    val regex = Regex("""([^/]+)\.kt$""")
    val compiler = JVMCompiler()
    var commonMethods: Coverage? = null
    File("tmp/arrays").walk().forEach {
        val sourceFilePath = it.absolutePath
        val matchResult = regex.find(sourceFilePath)
        if (matchResult != null) {
            val methodCoverage = CoverageComposer.composeFrom(compiler.getExecutionData(sourceFilePath), EntityType.METHODS, SegmentType.METHODS, false)
            commonMethods = if (commonMethods == null) methodCoverage.copy() else commonMethods!! and methodCoverage
            commonMethods!!.removeEmptyEntries()
        }
    }
    if (commonMethods != null) serializeCoverage(commonMethods!!, "tmp/commonMethods.json")
    System.exit(0)
    */

    /*
    val failureCommonMethods = deserializeCoverage("tmp/failureCommonMethods.cbor")[0]
    val successCommonMethods = deserializeCoverage("tmp/successCommonMethods.cbor")[0]
    val commonMethods = failureCommonMethods and successCommonMethods
    val failureExclusiveCommonMethods = failureCommonMethods - commonMethods
    failureExclusiveCommonMethods.removeEmptyEntries()
    System.exit(0)
    */

    /*
    val regex = Regex("""([^/]+)\.kt$""")
    var commonMethods: Coverage? = null
    File("/home/ruban/kotlin-samples/unchecked-crashes/").walk().forEach {
        val sourceFilePath = it.absolutePath
        val matchResult = regex.find(sourceFilePath)
        if (matchResult != null) {
            println("Isolating $sourceFilePath ....")
            val startTime = System.currentTimeMillis()
            val mutantCoverages = BugIsolator.isolate(sourceFilePath, BugType.BACKEND)
            println("   time spent: ${(System.currentTimeMillis() - startTime) / 1000.0 } ....")
            if (commonMethods == null) {
                commonMethods = mutantCoverages.failureCoverages[0]
            }
            commonMethods = commonMethods!! and mutantCoverages.failureCoverages
            commonMethods!!.removeEmptyEntries()
        }
    }
    if (commonMethods != null) serializeCoverage(commonMethods!!, "tmp/failureCommonMethods.cbor")
    System.exit(0)
    */

    /*
    val noise = deserializeCoverage("tmp/commonLines.json")[0]
    val coverages1 = deserializeMutantCoverages("tmp/line-coverages-set-s/group1duplicate0.cbor", "cbor")[0]
    val statisticsSet1 = EntityStatisticsSet(coverages1)
    val rankedList1 = statisticsSet1.rank(OchiaiRankingFormula)
    val coverages2 = deserializeMutantCoverages("tmp/line-coverages-set-s/group1duplicate1.cbor", "cbor")[0]
    val statisticsSet2 = EntityStatisticsSet(coverages2)
    val rankedList2 = statisticsSet2.rank(OchiaiRankingFormula)
    System.exit(0)
    */

    /* TODO mutation statistics
    val regex = Regex("""([^/]+)\.cbor$""")
    val mutationStatistics = mutableMapOf<String, List<MutationStatistics>>()
    val statisticsSets = mutableMapOf<String, EntityStatisticsSet>()
    File("tmp/mutation-statistics-set-n").walk().sortedBy { it.absolutePath }.forEach { it ->
        val coveragesFilePath = it.absolutePath
        val matchResult = regex.find(coveragesFilePath)
        if (matchResult != null) {
            val coveragesFileName = matchResult.groupValues[1]
            mutationStatistics[coveragesFileName] = MutationStatistics.deserialize(coveragesFilePath)
        }
    }
    File("tmp/method-coverages-set-n").walk().sortedBy { it.absolutePath }.forEach { it ->
        val coveragesFilePath = it.absolutePath
        val matchResult = regex.find(coveragesFilePath)
        if (matchResult != null) {
            println("Composing entity statistics set for $coveragesFilePath ....")
            val coveragesFileName = matchResult.groupValues[1]
            val mutantCoverages = deserializeMutantCoverages(coveragesFilePath)[0]
            statisticsSets[coveragesFileName] = EntityStatisticsSet(mutantCoverages)
        }
    }

    val collapsedMutationStatistics = CollapsedMutationStatistics()
    for ((name, set) in statisticsSets) {
        if (set.failures != 1) {
            collapsedMutationStatistics += mutationStatistics[name]!!
        }
    }

    collapsedMutationStatistics.removeIdenticalCoveragesStatistics()
    val percentages = collapsedMutationStatistics.percentages
    for ((name, mutationPercentages) in percentages) {
        println(name)
        println("   failures: ${mutationPercentages.first}%")
        println("   successes: ${mutationPercentages.second}%")
    }
    System.exit(0)
    */

    // TODO bug isolation results comparison
    val regex = Regex("""([^/]+)\.cbor$""")
    val failureNoise = deserializeCoverage("tmp/failureCommonMethods.cbor")[0]
    val statisticsSets = mutableMapOf<String, EntityStatisticsSet>()
    File("tmp/method-coverages-set-n").walk().sortedBy { it.absolutePath }.forEach { it ->
        val coveragesFilePath = it.absolutePath
        val matchResult = regex.find(coveragesFilePath)
        if (matchResult != null) {
            println("Composing entity statistics set for $coveragesFilePath ....")
            val coveragesFileName = matchResult.groupValues[1]
            val startTime = System.currentTimeMillis()
            val mutantCoverages = deserializeMutantCoverages(coveragesFilePath)[0]
            //mutantCoverages.keepFirstFailures(10)
            /*mutantCoverages.failureCoverages = mutantCoverages.failureCoverages.map { coverage -> coverage - failureNoise }*/
            statisticsSets[coveragesFileName] = EntityStatisticsSet(mutantCoverages)
            println("   time spent: ${(System.currentTimeMillis() - startTime) / 1000.0 }")
        }
    }
    //for ((_, set) in statisticsSets) {
    //    set.removeIrrelevantEntries()
    //}

    val rankedLists = mutableMapOf<String, RankedEntityList>()
    val rankingFormula = OchiaiRankingFormula
    for ((sampleName, set) in statisticsSets) {
        println("Ranking $sampleName entities with $rankingFormula ....")
        val startTime = System.currentTimeMillis()
        rankedLists[sampleName] = set.rank(rankingFormula)
        println("   time spent: ${(System.currentTimeMillis() - startTime) / 1000.0 }")
    }
    //for ((_, list) in rankedLists) {
    //    list.retainFirstElements(1000)
    //}

    class ComparisonResult(
        val names: List<String>,
        val result: Double
    )

    val analyzedPairs = mutableListOf<ComparisonResult>()
    for ((sample1Name, rankedList1) in rankedLists) {
        for ((sample2Name, rankedList2) in rankedLists) {
            if (sample1Name == sample2Name) continue
            if (analyzedPairs.find { sample1Name in it.names && sample2Name in it.names } == null) {
                println("Comparing $sample1Name and $sample2Name ....")
                val startTime = System.currentTimeMillis()
                //val prefixSize = min(rankedList1.size, rankedList2.size)
                //rankedList1.retainFirstElements(prefixSize)
                //rankedList2.retainFirstElements(prefixSize)
                analyzedPairs += ComparisonResult(listOf(sample1Name, sample2Name), rankedList1.cosineSimilarity(rankedList2))
                println("   time spent: ${(System.currentTimeMillis() - startTime) / 1000.0 }")
            }
        }
    }
    analyzedPairs.sortByDescending { it.result }

    val nameRegex = Regex("""(\d+)_.+""")
    for (analyzedPair in analyzedPairs) {
        val name1 = analyzedPair.names[0]
        val group1 = nameRegex.matchEntire(name1)!!.groupValues[1]
        val name2 = analyzedPair.names[1]
        val group2 = nameRegex.matchEntire(name2)!!.groupValues[1]
        print("${analyzedPair.result}")
        //print("${analyzedPair.names}: ${analyzedPair.result}")
        if (group1 == group2) print(" (DUPLICATES)")
        println()
    }

    System.exit(0)
    //*/

    /* TODO bug isolation
    val noise = deserializeCoverage("tmp/successCommonMethods.cbor")[0]
    CoverageComposer.defineNoise(noise)
    val regex = Regex("""(\d+/[^/]+)\.kt$""")
    File("/home/ruban/kotlin-samples/set-n/").walk().sortedBy { it.absolutePath }.forEach {
        val sourceFilePath = it.absolutePath
        val matchResult = regex.find(sourceFilePath)
        if (matchResult != null) {
            println("Isolating $sourceFilePath ....")
            val sourceFileName = matchResult.groupValues[1]
            val startTime = System.currentTimeMillis()
            val isolationResult = BugIsolator.isolate(sourceFilePath, BugType.BACKEND)
            serializeMutantCoverages(
                isolationResult.first,
                "tmp/method-coverages-set-n/${sourceFileName.replace("/", "_")}.cbor",
                Format.CBOR
            )
            MutationStatistics.serialize(
                isolationResult.second,
                "tmp/mutation-statistics-set-n/${sourceFileName.replace("/", "_")}.cbor",
                Format.CBOR
            )
            println("   time spent: ${(System.currentTimeMillis() - startTime) / 1000.0 } ....")
        }
    }
    System.exit(0)
    */

    /*
    if (!CompilerArgs.getPropAsBoolean("LOG")) {
        Logger.getRootLogger().level = Level.OFF
        Logger.getLogger("bugFinderLogger").level = Level.OFF
        Logger.getLogger("mutatorLogger").level = Level.OFF
        Logger.getLogger("reducerLogger").level = Level.OFF
        Logger.getLogger("transformationManagerLog").level = Level.OFF
    }

    val parser = ArgumentParsers.newFor("bbf").build()
    parser.addArgument("-r", "--reduce")
        .required(false)
        .help("Reduce mode")
    parser.addArgument("-f", "--fuzz")
        .required(false)
        .help("Fuzzing mode")
    parser.addArgument("-c", "--clean")
        .required(false)
        .action(Arguments.storeTrue())
        .help("Clean directories with bugs from bugs that are not reproduced")
    parser.addArgument("-d", "--database")
        .required(false)
        .action(Arguments.storeTrue())
        .help("Database updating")
    val arguments = parser.parseArgs(args)
    arguments.getString("reduce")?.let {
        val type = BBFProperties.getStringGroupWithoutQuotes("BUG_FOR_REDUCE").entries.first().value
        val backends = BBFProperties.getStringGroupWithoutQuotes("BACKEND_FOR_REDUCE").entries
        val compilers = backends.map { back ->
            when {
                back.key.startsWith("JVM") -> JVMCompiler(back.value)
                back.key.startsWith("JS") -> JSCompiler(back.value)
                else -> throw IllegalArgumentException("Illegal backend")
            }
        }
        val tmpPath = CompilerArgs.pathToTmpFile
        require(!File(it).isDirectory) { "Specify file to reducing" }
        File(tmpPath).writeText(File(it).readText())
        val res = when (type) {
            "DIFF_BEHAVIOR" -> Reducer.reduceDiffBehavior(tmpPath, compilers)
            "BACKEND_CRASH" -> Reducer.reduce(tmpPath, compilers.first()).first().toString()
            else -> throw IllegalArgumentException("Illegal type of bug")
        }
        println("Result of reducing:\n$res")
        exitProcess(0)
    }
    arguments.getString("fuzz")?.let {
        require(File(it).isDirectory) { "Specify directory to take files for mutation" }
        val file = File(it).listFiles()?.random() ?: throw IllegalArgumentException("Wrong directory")
        BugFinder(file.absolutePath).findBugsInFile()
        exitProcess(0)
    }
    if (arguments.getString("database") == "true") {
        NodeCollector(CompilerArgs.baseDir).collect()
        exitProcess(0)
    }
    if (arguments.getString("clean") == "true") {
        FalsePositivesDeleter().cleanDirs()
        exitProcess(0)
    }
    val file = File(CompilerArgs.baseDir).listFiles()?.random() ?: throw IllegalArgumentException("Wrong directory")
    BugFinder(file.absolutePath).findBugsInFile()
    exitProcess(0)
    */

}