package com.stepanov.bbf

import com.stepanov.bbf.bugfinder.executor.WitnessTestsCollector
import com.stepanov.bbf.bugfinder.isolation.NoBugFoundException
import com.stepanov.bbf.bugfinder.mutator.transformations.Transformation
import com.stepanov.bbf.reduktor.parser.PSICreator
import java.io.File

// TODO
fun filterSamplesByBugPresence(datasetPath: String, compilerArgs: String) {
    val fineSamples = mutableListOf<String>()
    val samplesWithNoBugs = mutableListOf<String>()
    val erroneousSamples = mutableListOf<String>()

    File(datasetPath).walk().sortedBy { it.absolutePath }.forEach { sourceFile ->
        val sourceFilePath = sourceFile.absolutePath
        kotlinSampleRegex.find(sourceFilePath)?.let { _ ->
            fun debug(e: Throwable) {
                isolationLogger.debug("@ $sourceFilePath")
                isolationLogger.debug("Exception: ${e.javaClass.name}")
                isolationLogger.debug(if (e.message.isNullOrEmpty()) "no message" else e.message)
                isolationLogger.debug("")
            }

            try {
                val creator = PSICreator("")
                val file = creator.getPSIForFile(sourceFilePath)
                Transformation.file = file
                WitnessTestsCollector(getCompiler(compilerArgs))
                fineSamples += sourceFilePath
            } catch (e: NoBugFoundException) {
                debug(e)
                samplesWithNoBugs += sourceFilePath
            } catch (e: Throwable) {
                debug(e)
                erroneousSamples += sourceFilePath
            }
        }
    }

    isolationLogger.debug("Fine samples (${fineSamples.size} items):\n${fineSamples.joinToString("\n")}\n\n")
    isolationLogger.debug("Samples with no bugs (${samplesWithNoBugs.size} items):\n${samplesWithNoBugs.joinToString("\n")}\n\n")
    isolationLogger.debug("Erroneous samples (${erroneousSamples.size} items):\n${erroneousSamples.joinToString("\n")}\n\n")
}

// TODO
fun filterSamplesByIdentity(datasetPath: String) {
    val allSamples = mutableSetOf<Pair<Sample, String>>()
    val samplesByGroup = mutableMapOf<String, MutableSet<Pair<String, String>>>()
    val filteredSamples = mutableListOf<Pair<Sample, Sample>>()
    val repeatedSamples = mutableListOf<Pair<Sample, Sample>>()

    File(datasetPath).walk().sortedBy { it.absolutePath }.forEach { sourceFile ->
        val sourceFilePath = sourceFile.absolutePath
        kotlinSampleRegex.find(sourceFilePath)?.let { matchResult ->
            val sampleID = matchResult.groupValues
            val sample = Sample(sampleID[1], sampleID[2])
            val code = sourceFile.readLines().joinToString("\n")
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
                val search = allSamples.find { s -> s.second == code }
                if (search != null) {
                    repeatedSamples += search.first to sample
                    return@forEach
                }
                samplesByGroup[sample.group] = mutableSetOf(sample.id to code)
                allSamples += sample to code
            }
        }
    }

    isolationLogger.debug("Same samples in one group (${filteredSamples.size} items):\n${filteredSamples.joinToString("\n")}\n\n")
    isolationLogger.debug("Same samples between different groups (${repeatedSamples.size} items):\n${repeatedSamples.joinToString("\n")}\n\n")
}

// TODO
fun calculateDatasetDifference(firstDataset: String, secondDataset: String) {

}
