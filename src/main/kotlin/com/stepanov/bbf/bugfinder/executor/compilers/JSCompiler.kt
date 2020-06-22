package com.stepanov.bbf.bugfinder.executor.compilers

import com.stepanov.bbf.bugfinder.executor.CommonCompiler
import com.stepanov.bbf.bugfinder.executor.CompilerArgs
import com.stepanov.bbf.bugfinder.executor.CompilingResult
import com.stepanov.bbf.bugfinder.executor.project.Project
import com.stepanov.bbf.bugfinder.util.Stream
import com.stepanov.bbf.bugfinder.util.copyFullJarImpl
import com.stepanov.bbf.bugfinder.util.writeRuntimeToJar
import com.stepanov.bbf.coverage.CompilerInstrumentation
import org.jetbrains.kotlin.cli.common.arguments.K2JSCompilerArguments
import org.jetbrains.kotlin.cli.js.K2JSCompiler
import org.jetbrains.kotlin.config.Services
import com.stepanov.bbf.reduktor.executor.KotlincInvokeStatus
import com.stepanov.bbf.reduktor.util.MsgCollector
import org.apache.commons.io.FileUtils
import org.apache.log4j.Logger
import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSourceLocation
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import java.io.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.jar.JarInputStream
import java.util.jar.JarOutputStream

class JSCompiler(private val arguments: String = "") : CommonCompiler() {

    override val compilerInfo: String
        get() = "JS $arguments"

    override val pathToCompiled: String
        get() = "tmp/tmp.js"

    override fun checkCompiling(project: Project): Boolean {
        val status = tryToCompile(project)
        return !MsgCollector.hasCompileError && !status.hasTimeout && !MsgCollector.hasException
    }

    override fun getErrorMessageWithLocation(project: Project): Pair<String, List<CompilerMessageSourceLocation>> {
        val status = tryToCompile(project)
        return status.combinedOutput to status.locations
    }

    override fun isCompilerBug(project: Project): Boolean =
        tryToCompile(project).hasException

    override fun compile(project: Project, includeRuntime: Boolean): CompilingResult {
        val projectWithMainFun = project.addMain()
        val path = projectWithMainFun.saveOrRemoveToTmp(true)
        val args = prepareArgs(projectWithMainFun, path, pathToCompiled)
        val status = executeCompiler(projectWithMainFun, args)
        if (status.hasException || status.hasTimeout || !status.isCompileSuccess) return CompilingResult(-1, "")
        val oldStr = FileReader(File(pathToCompiled)).readText()
        val newStr = "const kotlin = require(\"${CompilerArgs.pathToJsKotlinLib}/kotlin.js\");\n\n$oldStr"
        val fw = FileWriter(pathToCompiled, false)
        val bw = BufferedWriter(fw)
        bw.write(newStr)
        bw.close()
        return CompilingResult(0, pathToCompiled)
    }

    private fun prepareArgs(project: Project, path: String, destination: String): K2JSCompilerArguments {
        val destFile = File(destination)
        if (destFile.isFile) destFile.delete()
        else if (destFile.isDirectory) FileUtils.cleanDirectory(destFile)
        val projectArgs = project.getProjectSettingsAsCompilerArgs("JS") as K2JSCompilerArguments
        val compilerArgs =
            if (arguments.isEmpty())
                "$path -output $pathToCompiled".split(" ")
            else
                "$path $arguments -output $pathToCompiled".split(" ")
        projectArgs.apply { K2JSCompiler().parseArguments(compilerArgs.toTypedArray(), this) }
        projectArgs.libraries = CompilerArgs.jsStdLibPaths.joinToString(separator = ":")
        return projectArgs
    }

    private fun executeCompiler(project: Project, args: K2JSCompilerArguments): KotlincInvokeStatus {
        val compiler = K2JSCompiler()
        val services = Services.EMPTY
        MsgCollector.clear()
        val threadPool = Executors.newCachedThreadPool()
        val futureExitCode = threadPool.submit {
            CompilerInstrumentation.clearRecords()
            CompilerInstrumentation.shouldProbesBeRecorded = true
            compiler.exec(MsgCollector, services, args)
            CompilerInstrumentation.shouldProbesBeRecorded = false
        }
        var hasTimeout = false
        try {
            futureExitCode.get(10L, TimeUnit.SECONDS)
        } catch (ex: TimeoutException) {
            hasTimeout = true
            futureExitCode.cancel(true)
        } finally {
            project.saveOrRemoveToTmp(false)
        }
        val status = KotlincInvokeStatus(
            MsgCollector.crashMessages.joinToString("\n") +
                    MsgCollector.compileErrorMessages.joinToString("\n"),
            !MsgCollector.hasCompileError,
            MsgCollector.hasException,
            hasTimeout,
            MsgCollector.locations.toMutableList()
        )
        return status
    }

    override fun tryToCompile(project: Project): KotlincInvokeStatus {
        val path = project.saveOrRemoveToTmp(true)
        val trashDir = "${CompilerArgs.pathToTmpDir}/trash/"
        val args = prepareArgs(project, path, trashDir)
        return executeCompiler(project, args)
    }

    override fun exec(path: String, streamType: Stream): String = commonExec("node $path", streamType)

    private fun analyzeErrorMessage(msg: String): Boolean = !msg.split("\n").any { it.contains(": error:") }

    private val log = Logger.getLogger("compilerErrorsLog")

//    fun getErrorMessageWithLocation(pathToFile: String): Pair<String, List<CompilerMessageLocation>> {
//        val status = tryToCompile(pathToFile)
//        return status.combinedOutput to status.locations
//    }
//
//    fun checkCompiling(pathToFile: String): Boolean {
//        val status = tryToCompile(pathToFile)
//        return !MsgCollector.hasCompileError && !status.hasTimeout && !MsgCollector.hasException
//    }
//
////    fun isCompilerBug(pathToFile: String) =
////        tryToCompile(pathToFile).hasException
//
//
//    fun compile(path: String, includeRuntime: Boolean): CompilingResult {
//        File(pathToCompiled).delete()
//        MsgCollector.clear()
//        val args =
//            if (arguments.isEmpty())
//                "$path -output $pathToCompiled".split(" ")
//            else
//                "$path $arguments -output $pathToCompiled".split(" ")
//        val compiler = K2JSCompiler()
//        val compilerArgs = K2JSCompilerArguments().apply { K2JSCompiler().parseArguments(args.toTypedArray(), this) }
//        compilerArgs.libraries = CompilerArgs.jsStdLibPaths.joinToString(separator = ":")
//        val services = Services.EMPTY
//        val threadPool = Executors.newCachedThreadPool()
//        val futureExitCode = threadPool.submit {
//            compiler.exec(MsgCollector, services, compilerArgs)
//        }
//        var hasTimeout = false
//        try {
//            futureExitCode.get(10L, TimeUnit.SECONDS)
//        } catch (ex: TimeoutException) {
//            hasTimeout = true
//            futureExitCode.cancel(true)
//        }
//        val status = KotlincInvokeStatus(
//            MsgCollector.crashMessages.joinToString("\n") +
//                    MsgCollector.compileErrorMessages.joinToString("\n"),
//            !MsgCollector.hasCompileError,
//            MsgCollector.hasException,
//            hasTimeout
//        )
//        return if (!status.hasException && !status.hasTimeout) {
//            val oldStr = FileReader(File(pathToCompiled)).readText()
//            val newStr = "const kotlin = require(\"${CompilerArgs.pathToJsKotlinLib}/kotlin.js\");\n\n$oldStr"
//            val fw = FileWriter(pathToCompiled, false)
//            val bw = BufferedWriter(fw)
//            bw.write(newStr)
//            bw.close()
//            CompilingResult(0, pathToCompiled)
//        } else CompilingResult(-1, "")
//    }
//
//
//    fun tryToCompile(pathToFile: String): KotlincInvokeStatus {
//        File(pathToCompiled).delete()
//        MsgCollector.clear()
//        val args =
//            if (arguments.isEmpty())
//                "$pathToFile -libraries ${CompilerArgs.jsStdLibPaths.joinToString(":")} -output $pathToCompiled"
//            else
//                "$pathToFile $arguments -libraries ${CompilerArgs.jsStdLibPaths.joinToString(":")} -output $pathToCompiled"
//        val compiler = K2JSCompiler()
//        val compilerArgs =
//            K2JSCompilerArguments().apply { K2JSCompiler().parseArguments(args.split(" ").toTypedArray(), this) }
//        val services = Services.EMPTY
//        val threadPool = Executors.newCachedThreadPool()
//        val futureExitCode = threadPool.submit {
//            compiler.exec(MsgCollector, services, compilerArgs)
//        }
//        var hasTimeout = false
//        try {
//            futureExitCode.get(10L, TimeUnit.SECONDS)
//        } catch (ex: TimeoutException) {
//            hasTimeout = true
//            futureExitCode.cancel(true)
//        }
//        val status = KotlincInvokeStatus(
//            MsgCollector.crashMessages.joinToString("\n") +
//                    MsgCollector.compileErrorMessages.joinToString("\n"),
//            !MsgCollector.hasCompileError,
//            MsgCollector.hasException,
//            hasTimeout,
//            MsgCollector.locations.toMutableList()
//        )
//        File(pathToCompiled).delete()
//        return status
//    }
//
//    override fun checkCompiling(project: Project): Boolean {
//        TODO("Not yet implemented")
//    }
//
//    override fun getErrorMessageWithLocation(project: Project): Pair<String, List<CompilerMessageLocation>> {
//        TODO("Not yet implemented")
//    }
//
//    override fun tryToCompile(project: Project): KotlincInvokeStatus {
//        TODO("Not yet implemented")
//    }
//
//    override fun isCompilerBug(project: Project): Boolean {
//        TODO("Not yet implemented")
//    }
//
//    override fun compile(project: Project, includeRuntime: Boolean): CompilingResult {
//        TODO("Not yet implemented")
//    }
//
//    override fun exec(path: String, streamType: Stream): String = commonExec("node $path", streamType)
//
//    //    override fun compile(path: String): CompilingResult {
////        val command =
////                if (arguments.isEmpty())
////                    "${CompilerArgs.pathToKotlincJS} $path -libraries ${CompilerArgs.jsStdLibPaths.joinToString(":")} -output $pathToCompiled\n"
////                else
////                    "${CompilerArgs.pathToKotlincJS} $path $arguments -libraries ${CompilerArgs.jsStdLibPaths.joinToString(":")} -output $pathToCompiled\n"
////        val status: String
////        try {
////            status = commonExec(command, Stream.BOTH)
////        } catch (e: Exception) {
////            return CompilingResult(-1, "")
////        }
////        val isSuccess = analyzeErrorMessage(status)
////        return if (isSuccess) {
////            val oldStr = FileReader(File(pathToCompiled)).readText()
////            val newStr = "const kotlin = require(\"${CompilerArgs.pathToJsKotlinLib}/kotlin.js\");\n\n$oldStr"
////            val fw = FileWriter(pathToCompiled, false)
////            val bw = BufferedWriter(fw)
////            bw.write(newStr)
////            bw.close()
////            CompilingResult(0, pathToCompiled)
////        } else {
////            CompilingResult(-1, "")
////        }
////    }

}