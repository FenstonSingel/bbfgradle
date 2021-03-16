package com.stepanov.bbf.isolation.testbed

import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch
import java.io.File

fun getStacktrace(sourceFilePath: String, sample: Sample): String? {
    isolationTestbedLogger.debug("compiling $sourceFilePath to get stacktrace data")
    isolationTestbedLogger.debug("code:\n${File("tmp/tmp.kt").readText()}")
    isolationTestbedLogger.debug("")
    val stacktrace = defaultBugInfo.firstCompiler.getErrorMessage("tmp/tmp.kt")
        .split("Cause:")
        .last()
        .split("\n")
        .map { it.trim() }
        .filter { it.startsWith("at ") }
        .joinToString("\n") { it.replaceFirst("at ", "") }
    isolationTestbedLogger.debug("stacktrace for $sourceFilePath obtained successfully")
    isolationTestbedLogger.debug("")
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