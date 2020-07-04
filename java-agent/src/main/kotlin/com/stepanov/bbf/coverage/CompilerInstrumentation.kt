package com.stepanov.bbf.coverage

import org.objectweb.asm.Opcodes
import kotlin.IllegalArgumentException

object CompilerInstrumentation {

    @JvmStatic var shouldClassesBeInstrumented: Boolean = true

    @JvmStatic var shouldProbesBeRecorded: Boolean = false

    val entryProbes = mutableMapOf<String, Int>()

    val branchProbes = mutableMapOf<String, MutableMap<Int, Int>>()

    val isEmpty: Boolean get() = entryProbes.isEmpty() && branchProbes.isEmpty()

    @JvmStatic fun recordMethodExecution(id: String) {
        startPerformanceTimer()
        if (shouldProbesBeRecorded) {
            entryProbes.merge(id, 1) { previous, one -> previous + one }
        }
        pausePerformanceTimer()
    }

    private fun recordBranchExecution(insn_id: String, result: Int) {
        if (insn_id in branchProbes) {
            val probeResults = branchProbes[insn_id]!!
            probeResults.merge(result, 1) { previous, one -> previous + one }
        } else {
            branchProbes[insn_id] = mutableMapOf(result to 1)
        }
    }

    @JvmStatic fun recordUnaryRefCmp(a: Any?, insn_id: String) {
        startPerformanceTimer()
        if (shouldProbesBeRecorded) {
            val result = if (a == null) 0 else 1
            recordBranchExecution(insn_id, result)
        }
        pausePerformanceTimer()
    }

    @JvmStatic fun recordBinaryRefCmp(a: Any, b: Any, insn_id: String) {
        startPerformanceTimer()
        if (shouldProbesBeRecorded) {
            val result = if (a !== b) 0 else 1
            recordBranchExecution(insn_id, result)
        }
        pausePerformanceTimer()
    }

    @JvmStatic fun recordUnaryIntCmp(a: Int, insn_id: String, opcode: Int) {
        startPerformanceTimer()
        if (shouldProbesBeRecorded) {
            val result = if (when (opcode) {
                        Opcodes.IFEQ -> a == 0
                        Opcodes.IFNE -> a != 0
                        Opcodes.IFLT -> a < 0
                        Opcodes.IFLE -> a <= 0
                        Opcodes.IFGT -> a > 0
                        Opcodes.IFGE -> a >= 0
                        else -> throw IllegalArgumentException("An inappropriate opcode was provided.")
                    }) 1 else 0
            recordBranchExecution(insn_id, result)
        }
        pausePerformanceTimer()
    }

    @JvmStatic fun recordBinaryIntCmp(a: Int, b: Int, insn_id: String, opcode: Int) {
        startPerformanceTimer()
        if (shouldProbesBeRecorded) {
            val result = if (when (opcode) {
                        Opcodes.IF_ICMPEQ -> a == b
                        Opcodes.IF_ICMPNE -> a != b
                        Opcodes.IF_ICMPLT -> a < b
                        Opcodes.IF_ICMPLE -> a <= b
                        Opcodes.IF_ICMPGT -> a > b
                        Opcodes.IF_ICMPGE -> a >= b
                        else -> throw IllegalArgumentException("An inappropriate opcode was provided.")
                    }) 1 else 0
            recordBranchExecution(insn_id, result)
        }
        pausePerformanceTimer()
    }

    @JvmStatic fun recordTableSwitch() {
        startPerformanceTimer()
        if (shouldProbesBeRecorded) {
            // TODO
        }
        pausePerformanceTimer()
    }

    @JvmStatic fun recordLookUpSwitch() {
        startPerformanceTimer()
        if (shouldProbesBeRecorded) {
            // TODO
        }
        pausePerformanceTimer()
    }

    var instrumentationTimer = 0L
        private set

    @JvmStatic fun updateInstrumentationTimer(newTime: Long) {
        instrumentationTimer += newTime
    }

    var performanceTimer = 0L
        private set

    private fun startPerformanceTimer() {
        performanceTimer -= System.currentTimeMillis()
    }

    private fun pausePerformanceTimer() {
        performanceTimer += System.currentTimeMillis()
    }

    fun clearRecords() {
        entryProbes.clear()
        branchProbes.clear()
        performanceTimer = 0L
    }

}