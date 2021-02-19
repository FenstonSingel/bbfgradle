package com.stepanov.bbf.bugfinder.isolation

import com.stepanov.bbf.bugfinder.executor.CommonCompiler
import com.stepanov.bbf.bugfinder.executor.compilers.JSCompiler
import com.stepanov.bbf.bugfinder.executor.compilers.JVMCompiler
import com.stepanov.bbf.bugfinder.manager.BugType
import kotlin.IllegalArgumentException

data class BugInfo(
        val type: BugType,
        val compilersInfo: List<Pair<String, String>>
) {
    private fun createCompiler(info: Pair<String, String>): CommonCompiler = when (info.first) {
        "JVM" -> JVMCompiler(info.second)
        "JS" -> JSCompiler(info.second)
        else -> throw IllegalArgumentException("Unknown compiler type detected.")
    }

    val firstCompiler: CommonCompiler get() = createCompiler(compilersInfo.first())

    val compilers: List<CommonCompiler> get() = compilersInfo.map { settings -> createCompiler(settings) }
}