package com.stepanov.bbf.bugfinder.executor.compilers

import com.stepanov.bbf.bugfinder.executor.CommonCompiler
import com.stepanov.bbf.bugfinder.executor.CompilerArgs
import com.stepanov.bbf.bugfinder.executor.CompilingResult
import com.stepanov.bbf.bugfinder.util.Stream
import com.stepanov.bbf.coverage.CompilerInstrumentation
import org.jetbrains.kotlin.cli.common.arguments.K2JSCompilerArguments
import org.jetbrains.kotlin.cli.js.K2JSCompiler
import org.jetbrains.kotlin.config.Services
import com.stepanov.bbf.reduktor.executor.KotlincInvokeStatus
import com.stepanov.bbf.reduktor.util.MsgCollector
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import java.io.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class JSCompiler(private val arguments: String = "") : CommonCompiler() {

    override val compilerInfo: String
        get() = "JS $arguments"

    override val pathToCompiled: String
        get() = "tmp/tmp.js"

    override fun getErrorMessageWithLocation(pathToFile: String): Pair<String, List<CompilerMessageLocation>> {
        val status = tryToCompile(pathToFile)
        return status.combinedOutput to status.locations
    }

    override fun checkCompiling(pathToFile: String): Boolean {
        val status = tryToCompile(pathToFile)
        return !MsgCollector.hasCompileError && !status.hasTimeout && !MsgCollector.hasException
    }

    override fun isCompilerBug(pathToFile: String) =
        tryToCompile(pathToFile).hasException

    override fun compile(path: String): CompilingResult {
        File(pathToCompiled).delete()
        MsgCollector.clear()
        val args =
            if (arguments.isEmpty())
                "$path -output $pathToCompiled".split(" ")
            else
                "$path $arguments -output $pathToCompiled".split(" ")
        val compiler = K2JSCompiler()
        val compilerArgs = K2JSCompilerArguments().apply { K2JSCompiler().parseArguments(args.toTypedArray(), this) }
        compilerArgs.libraries = CompilerArgs.jsStdLibPaths.joinToString(separator = ":")
        val services = Services.EMPTY

        // TODO Compilation timeouts (deleted in this branch) are a potential problem in the future.
        CompilerInstrumentation.clearRecords()
        CompilerInstrumentation.shouldProbesBeRecorded = true
        compiler.exec(MsgCollector, services, compilerArgs)
        CompilerInstrumentation.shouldProbesBeRecorded = false

        val status = KotlincInvokeStatus(
            MsgCollector.crashMessages.joinToString("\n") +
                    MsgCollector.compileErrorMessages.joinToString("\n"),
            !MsgCollector.hasCompileError,
            MsgCollector.hasException,
            false
        )

        return if (!status.hasException && !status.hasTimeout) {
            val oldStr = FileReader(File(pathToCompiled)).readText()
            val newStr = "const kotlin = require(\"${CompilerArgs.pathToJsKotlinLib}/kotlin.js\");\n\n$oldStr"
            val fw = FileWriter(pathToCompiled, false)
            val bw = BufferedWriter(fw)
            bw.write(newStr)
            bw.close()
            CompilingResult(0, pathToCompiled)
        } else CompilingResult(-1, "")
    }


    override fun tryToCompile(pathToFile: String): KotlincInvokeStatus {
        File(pathToCompiled).delete()
        MsgCollector.clear()
        val args =
            if (arguments.isEmpty())
                "$pathToFile -libraries ${CompilerArgs.jsStdLibPaths.joinToString(":")} -output $pathToCompiled"
            else
                "$pathToFile $arguments -libraries ${CompilerArgs.jsStdLibPaths.joinToString(":")} -output $pathToCompiled"
        val compiler = K2JSCompiler()
        val compilerArgs =
            K2JSCompilerArguments().apply { K2JSCompiler().parseArguments(args.split(" ").toTypedArray(), this) }
        val services = Services.EMPTY

        // Compilation timeouts (deleted in this branch) are a potential problem in the future.
        CompilerInstrumentation.clearRecords()
        CompilerInstrumentation.shouldProbesBeRecorded = true
        compiler.exec(MsgCollector, services, compilerArgs)
        CompilerInstrumentation.shouldProbesBeRecorded = false

        val status = KotlincInvokeStatus(
                MsgCollector.crashMessages.joinToString("\n") +
                        MsgCollector.compileErrorMessages.joinToString("\n"),
                !MsgCollector.hasCompileError,
                MsgCollector.hasException,
                false
        )

        File(pathToCompiled).delete()
        return status
    }

    override fun exec(path: String, streamType: Stream): String = commonExec("node $path", streamType)

//    override fun compile(path: String): CompilingResult {
//        val command =
//                if (arguments.isEmpty())
//                    "${CompilerArgs.pathToKotlincJS} $path -libraries ${CompilerArgs.jsStdLibPaths.joinToString(":")} -output $pathToCompiled\n"
//                else
//                    "${CompilerArgs.pathToKotlincJS} $path $arguments -libraries ${CompilerArgs.jsStdLibPaths.joinToString(":")} -output $pathToCompiled\n"
//        val status: String
//        try {
//            status = commonExec(command, Stream.BOTH)
//        } catch (e: Exception) {
//            return CompilingResult(-1, "")
//        }
//        val isSuccess = analyzeErrorMessage(status)
//        return if (isSuccess) {
//            val oldStr = FileReader(File(pathToCompiled)).readText()
//            val newStr = "const kotlin = require(\"${CompilerArgs.pathToJsKotlinLib}/kotlin.js\");\n\n$oldStr"
//            val fw = FileWriter(pathToCompiled, false)
//            val bw = BufferedWriter(fw)
//            bw.write(newStr)
//            bw.close()
//            CompilingResult(0, pathToCompiled)
//        } else {
//            CompilingResult(-1, "")
//        }
//    }

}