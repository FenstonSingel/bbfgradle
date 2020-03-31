package com.stepanov.bbf.coverage.extraction

import org.jacoco.core.runtime.IRuntime
import org.jacoco.core.runtime.RuntimeData

class CompilerInstrumentation {

    companion object {
        lateinit var jacocoRuntime: IRuntime
        lateinit var runtimeData: RuntimeData
        var shouldClassesBeInstrumented: Boolean = false
    }

}