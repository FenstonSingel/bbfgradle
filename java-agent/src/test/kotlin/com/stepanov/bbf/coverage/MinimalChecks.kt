package com.stepanov.bbf.coverage

import com.stepanov.bbf.coverage.instrumentation.Transformer
import org.junit.Test
import kotlin.test.*

/* These tests do very little: they simply check if *any* changes were or were not done.
 * However, they are at least able to throw exceptions, unlike the ones where Java agent is
 * used as a, well, Java agent. */
class MinimalChecks {

    @Test
    fun minimalChecks() {
        checkIfTransforms("org/jetbrains/kotlin/fir/RelevantTestKlass")
        checkIfPreserves("org/jetbrains/not/kotlin/UselessTestKlass")
        checkIfPreserves("org/jetbrains/kotlin/cli/AnotherUselessTestKlass")
    }

    private fun getClassFiles(className: String): Pair<ByteArray, ByteArray> {
        val resource = ClassLoader.getSystemResourceAsStream("$className.class")
        val initClassFile = resource?.readBytes() ?: error("No class named $className was loaded.")
        val newClassFile = Transformer().transform(className, initClassFile)
        return initClassFile to newClassFile
    }

    private fun checkIfTransforms(className: String) {
        val (initClassFile, newClassFile) = getClassFiles(className)
        assertNotEquals(initClassFile, newClassFile)
    }

    private fun checkIfPreserves(className: String) {
        val (_, newClassFile) = getClassFiles(className)
        assertNull(newClassFile)
    }

}