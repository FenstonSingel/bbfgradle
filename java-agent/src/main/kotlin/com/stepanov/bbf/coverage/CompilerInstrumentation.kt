package com.stepanov.bbf.coverage

import org.objectweb.asm.Opcodes
import kotlin.IllegalArgumentException

// TODO Create some sort of verification that both CompilerInstrumentation files are identical.
// (or, alternatively, come up with a solution that doesn't require having two identical source files)
object CompilerInstrumentation {

    enum class CoverageType {
        METHOD, BRANCH;
    }

    @JvmStatic var coverageType: CoverageType = CoverageType.METHOD

    @JvmStatic var shouldProbesBeRecorded: Boolean = false

    val methodProbes = mutableMapOf<String, Int>()

    val branchProbes = mutableMapOf<String, MutableMap<String, Int>>()

    val isEmpty: Boolean get() =
        when (coverageType) {
            CoverageType.METHOD -> methodProbes.isEmpty()
            CoverageType.BRANCH -> branchProbes.isEmpty()
        }

    fun clearRecords() {
        when (coverageType) {
            CoverageType.METHOD -> methodProbes.clear()
            CoverageType.BRANCH -> branchProbes.clear()
        }
        instrumentationPerformanceTime = 0L
    }

    @JvmStatic fun recordMethodExecution(id: String) {
        val timerStart = System.currentTimeMillis()
        if (shouldProbesBeRecorded) {
            methodProbes.merge(id, 1) { previous, one -> previous + one }
        }
        updateInstrumentationPerformanceTimer(System.currentTimeMillis() - timerStart)
    }

    private fun recordBranchExecution(insn_id: String, result: String) {
        if (insn_id in branchProbes) {
            val probeResults = branchProbes[insn_id]!!
            probeResults.merge(result, 1) { previous, one -> previous + one }
        } else {
            branchProbes[insn_id] = mutableMapOf(result to 1)
        }
    }

    @JvmStatic fun recordUnaryRefCmp(a: Any?, insn_id: String) {
        val timerStart = System.currentTimeMillis()
        if (shouldProbesBeRecorded) {
            val result = if (a == null) "A" else "B"
            recordBranchExecution(insn_id, result)
        }
        updateInstrumentationPerformanceTimer(System.currentTimeMillis() - timerStart)
    }

    @JvmStatic fun recordBinaryRefCmp(a: Any?, b: Any?, insn_id: String) {
        val timerStart = System.currentTimeMillis()
        if (shouldProbesBeRecorded) {
            val result = if (a !== b) "A" else "B"
            recordBranchExecution(insn_id, result)
        }
        updateInstrumentationPerformanceTimer(System.currentTimeMillis() - timerStart)
    }

    @JvmStatic fun recordUnaryIntCmp(a: Int, insn_id: String, opcode: Int) {
        val timerStart = System.currentTimeMillis()
        if (shouldProbesBeRecorded) {
            val result = if (when (opcode) {
                    Opcodes.IFEQ -> a == 0
                    Opcodes.IFNE -> a != 0
                    Opcodes.IFLT -> a < 0
                    Opcodes.IFLE -> a <= 0
                    Opcodes.IFGT -> a > 0
                    Opcodes.IFGE -> a >= 0
                    else -> throw IllegalArgumentException("An inappropriate opcode was provided.")
                }) "A" else "B"
            recordBranchExecution(insn_id, result)
        }
        updateInstrumentationPerformanceTimer(System.currentTimeMillis() - timerStart)
    }

    @JvmStatic fun recordBinaryIntCmp(a: Int, b: Int, insn_id: String, opcode: Int) {
        val timerStart = System.currentTimeMillis()
        if (shouldProbesBeRecorded) {
            val result = if (when (opcode) {
                    Opcodes.IF_ICMPEQ -> a == b
                    Opcodes.IF_ICMPNE -> a != b
                    Opcodes.IF_ICMPLT -> a < b
                    Opcodes.IF_ICMPLE -> a <= b
                    Opcodes.IF_ICMPGT -> a > b
                    Opcodes.IF_ICMPGE -> a >= b
                    else -> throw IllegalArgumentException("An inappropriate opcode was provided.")
                }) "A" else "B"
            recordBranchExecution(insn_id, result)
        }
        updateInstrumentationPerformanceTimer(System.currentTimeMillis() - timerStart)
    }

    private val tableSwitches = mutableMapOf<String, Pair<Int, Int>>()

    @JvmStatic fun rememberTableSwitch(insn_id: String, min: Int, max: Int) {
        tableSwitches[insn_id] = min to max
    }

    @JvmStatic fun recordTableSwitch(key: Int, insn_id: String) {
        val timerStart = System.currentTimeMillis()
        if (shouldProbesBeRecorded) {
            val (min, max) = tableSwitches[insn_id]!!
            val result = if (key < min || key > max) "DFLT" else key.toString()
            recordBranchExecution(insn_id, result)
        }
        updateInstrumentationPerformanceTimer(System.currentTimeMillis() - timerStart)
    }

    private val lookupSwitches = mutableMapOf<String, IntArray>()

    @JvmStatic fun rememberLookupSwitch(insn_id: String, keys: IntArray) {
        lookupSwitches[insn_id] = keys
    }

    @JvmStatic fun recordLookupSwitch(key: Int, insn_id: String) {
        val timerStart = System.currentTimeMillis()
        if (shouldProbesBeRecorded) {
            val keys = lookupSwitches[insn_id]!!
            val result = if (key in keys) key.toString() else "DFLT"
            recordBranchExecution(insn_id, result)
        }
        updateInstrumentationPerformanceTimer(System.currentTimeMillis() - timerStart)
    }

    var timeSpentOnInstrumentation = 0L
        private set

    @JvmStatic fun increaseTimeSpentOnInstrumentation(newTime: Long) {
        timeSpentOnInstrumentation += newTime
    }

    var instrumentationPerformanceTime = 0L
        private set

    private fun updateInstrumentationPerformanceTimer(newTime: Long) {
        instrumentationPerformanceTime += newTime
    }

}