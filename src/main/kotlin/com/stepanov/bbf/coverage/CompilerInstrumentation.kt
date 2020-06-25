package com.stepanov.bbf.coverage

object CompilerInstrumentation {

    @JvmStatic var shouldClassesBeInstrumented: Boolean = true

    @JvmStatic var shouldProbesBeRecorded: Boolean = false

    val probes = mutableMapOf<String, Int>()

    val isEmpty: Boolean get() = probes.isEmpty()

    @JvmStatic fun recordProbeExecution(id: String) {
        startPerformanceTimer()

        if (shouldProbesBeRecorded) {
            probes.merge(id, 1) { previous, one -> previous + one }
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
        probes.clear()
        performanceTimer = 0L
    }

}