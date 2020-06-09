package com.stepanov.bbf.bugfinder.executor.checkers

import com.intellij.psi.PsiErrorElement
import com.stepanov.bbf.bugfinder.executor.CommonCompiler
import com.stepanov.bbf.bugfinder.executor.project.BBFFile
import com.stepanov.bbf.bugfinder.executor.project.Project
import com.stepanov.bbf.bugfinder.manager.BugType
import com.stepanov.bbf.bugfinder.mutator.transformations.Factory
import com.stepanov.bbf.bugfinder.util.checkCompilingForAllBackends
import com.stepanov.bbf.reduktor.executor.error.Error
import com.stepanov.bbf.reduktor.util.containsChildOfType
import org.apache.log4j.Logger
import org.jetbrains.kotlin.psi.KtPsiFactory

class DiffBehaviorChecker(
    override val project: Project,
    override val curFile: BBFFile,
    private val compilers: List<CommonCompiler>
) : MultiCompilerCrashChecker(project, curFile, null, BugType.DIFFBEHAVIOR) {

    val prevResults: MutableList<List<String>> = mutableListOf()

    init {
        val results = compileAndGetExecResult()
        results.forEach { pair ->
            val bblocks = pair.second.split("\n").filter { it.isNotEmpty() }
            prevResults.add(bblocks)
        }
    }

    private fun compileAndGetExecResult(): List<Pair<CommonCompiler, String>> = compileAndGetExecResult(project)
    private fun compileAndGetExecResult(project: Project): List<Pair<CommonCompiler, String>> {
        val results = mutableListOf<Pair<CommonCompiler, String>>()
        for (comp in compilers) {
            val status = comp.compile(project)
            if (status.status == -1)
                return listOf()
            val res = comp.exec(status.pathToCompiled)
            log.debug("Result of ${comp.compilerInfo}: $res\n")
            results.add(comp to res.trim())
        }
        return results
    }

    @Deprecated("")
    override fun checkTest(text: String): Boolean {
        val project = Project.createFromCode(text)
        val projectHash = text.trim().hashCode()
        val preCheck = isAlreadyCheckedOrWrong(projectHash, project.files.first())
        if (preCheck.first) return preCheck.second
        val res = isSameDiffBehavior(project)
        alreadyChecked[projectHash] = res
        return res
    }

    private fun isSameDiffBehavior(): Boolean = isSameDiffBehavior(project)

    @Deprecated("")
    private fun isSameDiffBehavior(project: Project): Boolean {
        if (!compilers.checkCompilingForAllBackends(project)) {
            log.debug("Cannot compile with main")
            return false
        }
        log.debug("Executing traced code:\n$project")
        val results = compileAndGetExecResult(project)
        if (results.isEmpty()) return false
        val backup = prevResults.map { it }
        for ((ind, res) in results.withIndex()) {
            val prevRes = prevResults[ind]
            val curRes = res.second.split("\n").filter { it.isNotEmpty() }
            log.debug("prevRes for ${res.first.compilerInfo}: $prevRes\ncurRes: $curRes\n\n")
            if (curRes.any { !prevRes.contains(it) }) {
                for ((j, b) in backup.withIndex()) {
                    prevResults[j] = b
                }
                return false
            }
            prevResults[ind] = curRes
        }
        val set = results.map {
            it.second
                .split("\n")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .joinToString("\n")
        }.toSet()
        val res = results.size == set.size
        log.debug("Result = ${res}")
        if (!res) {
            for ((ind, b) in backup.withIndex()) {
                prevResults[ind] = b
            }
        }
        return res
    }


    override fun checkTest(): Boolean {
        val preCheck = isAlreadyCheckedOrWrong()
        if (preCheck.first) return preCheck.second
        val res = isSameDiffBehavior()
        alreadyChecked[projectHash] = res
        return res
    }

}