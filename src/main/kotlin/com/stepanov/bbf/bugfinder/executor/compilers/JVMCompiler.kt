package com.stepanov.bbf.bugfinder.executor.compilers

import com.stepanov.bbf.bugfinder.executor.CommonCompiler
import com.stepanov.bbf.bugfinder.executor.CompilerArgs
import com.stepanov.bbf.bugfinder.executor.CompilingResult
import com.stepanov.bbf.bugfinder.util.Stream
import com.stepanov.bbf.bugfinder.util.copyFullJarImpl
import com.stepanov.bbf.bugfinder.util.writeRuntimeToJar
import com.stepanov.bbf.coverage.extraction.CompilerInstrumentation
import com.stepanov.bbf.reduktor.executor.KotlincInvokeStatus
import com.stepanov.bbf.reduktor.util.MsgCollector
import org.apache.commons.io.FileUtils
import org.apache.log4j.Logger
import org.jacoco.core.data.ExecutionDataStore
import org.jacoco.core.data.SessionInfoStore
import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import org.jetbrains.kotlin.config.IncrementalCompilation
import org.jetbrains.kotlin.config.Services
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.jar.JarInputStream
import java.util.jar.JarOutputStream

class JVMCompiler(private val arguments: String = "") : CommonCompiler() {
    override val compilerInfo: String
        get() = "JVM $arguments"

    override val pathToCompiled: String
        get() = "tmp/tmp.jar"

    override fun checkCompiling(pathToFile: String): Boolean {
        val status = tryToCompile(pathToFile)
        return !MsgCollector.hasCompileError && !status.hasTimeout && !MsgCollector.hasException
    }

    override fun getErrorMessageWithLocation(pathToFile: String): Pair<String, List<CompilerMessageLocation>> {
        val status = tryToCompile(pathToFile)
        return status.combinedOutput to status.locations
    }

    override fun isCompilerBug(pathToFile: String): Boolean =
        tryToCompile(pathToFile).hasException

//    override fun compile(path: String): CompilingResult {
//        val kotlinc = CompilerArgs.pathToKotlinc
//        val command =
//            if (arguments.isEmpty())
//                "$kotlinc $path -include-runtime -d $pathToCompiled"
//            else
//                "$kotlinc $path -include-runtime $arguments -d $pathToCompiled"
//        val status: String
//        try {
//            status = commonExec(command, Stream.BOTH)
//        } catch (e: Exception) {
//            return CompilingResult(-1, "")
//        }
//        return if (analyzeErrorMessage(status)) CompilingResult(
//            0,
//            pathToCompiled
//        ) else CompilingResult(-1, "")kotlin
//    }

    override fun getExecutionDataWithStatus(pathToFile: String): Pair<Boolean, ExecutionDataStore> {
        val trashDir = "tmp/trash/"
        //Clean dir
        if (File(trashDir).exists())
            FileUtils.cleanDirectory(File(trashDir))
        val args =
            if (arguments.isEmpty())
                "$pathToFile -d $trashDir".split(" ")
            else
                "$pathToFile $arguments -d $trashDir".split(" ")
        val compiler = K2JVMCompiler()
        val compilerArgs = K2JVMCompilerArguments().apply { K2JVMCompiler().parseArguments(args.toTypedArray(), this) }
        if (CompilerArgs.classpath.isNotEmpty())
            compilerArgs.classpath = CompilerArgs.classpath
        else
            compilerArgs.classpath =
                "${CompilerArgs.jvmStdLibPaths.joinToString(
                    postfix = ":",
                    separator = ":"
                )}:${System.getProperty("java.class.path")}"

        compilerArgs.jdkHome = CompilerArgs.jdkHome
        compilerArgs.jvmTarget = CompilerArgs.jvmTarget
        IncrementalCompilation.setIsEnabledForJvm(true)

        val services = Services.EMPTY
        MsgCollector.clear()

        CompilerInstrumentation.runtimeData.collect(ExecutionDataStore(), SessionInfoStore(), true)
        CompilerInstrumentation.shouldClassesBeInstrumented = true
        compiler.exec(MsgCollector, services, compilerArgs)
        CompilerInstrumentation.shouldClassesBeInstrumented = false
        val executionData = ExecutionDataStore()
        CompilerInstrumentation.runtimeData.collect(executionData, SessionInfoStore(), false)

        return MsgCollector.hasException to executionData
    }

    override fun compile(path: String): CompilingResult {
        val threadPool = Executors.newCachedThreadPool()
        val tmpJar = "$pathToCompiled.jar"

        val args = if (arguments.isEmpty())
            "$path -d $tmpJar"
        else
            "$path $arguments -d $tmpJar"
        val compiler = K2JVMCompiler()
        val compilerArgs =
            K2JVMCompilerArguments().apply { K2JVMCompiler().parseArguments(args.split(" ").toTypedArray(), this) }
        if (CompilerArgs.classpath.isNotEmpty())
            compilerArgs.classpath = CompilerArgs.classpath
        else
            compilerArgs.classpath =
                "${CompilerArgs.jvmStdLibPaths.joinToString(
                    postfix = ":",
                    separator = ":"
                )}:${System.getProperty("java.class.path")}"

        compilerArgs.jdkHome = CompilerArgs.jdkHome
        compilerArgs.jvmTarget = CompilerArgs.jvmTarget
        IncrementalCompilation.setIsEnabledForJvm(true)

        val services = Services.EMPTY
        MsgCollector.clear()
        val futureExitCode = threadPool.submit {
            compiler.exec(MsgCollector, services, compilerArgs)
        }
        var hasTimeout = false
        try {
            futureExitCode.get(10L, TimeUnit.SECONDS)
        } catch (ex: TimeoutException) {
            hasTimeout = true
            futureExitCode.cancel(true)
        }

        val status = KotlincInvokeStatus(
            MsgCollector.crashMessages.joinToString("\n") +
                    MsgCollector.compileErrorMessages.joinToString("\n"),
            !MsgCollector.hasCompileError,
            MsgCollector.hasException,
            hasTimeout
        )
        if (status.hasException || status.hasTimeout || !status.isCompileSuccess) return CompilingResult(-1, "")
        val res = File(pathToCompiled)
        val input = JarInputStream(File(tmpJar).inputStream())
        val output = JarOutputStream(res.outputStream(), input.manifest)
        copyFullJarImpl(output, File(tmpJar))
        CompilerArgs.jvmStdLibPaths.forEach { writeRuntimeToJar(it, output) }
        output.finish()
        input.close()
        File(tmpJar).delete()
        return CompilingResult(0, pathToCompiled)
    }

    override fun tryToCompile(pathToFile: String): KotlincInvokeStatus {
        val threadPool = Executors.newCachedThreadPool()
        val trashDir = "tmp/trash/"
        //Clean dir
        if (File(trashDir).exists())
            FileUtils.cleanDirectory(File(trashDir))
        val args =
            if (arguments.isEmpty())
                "$pathToFile -d $trashDir".split(" ")
            else
                "$pathToFile $arguments -d $trashDir".split(" ")
        val compiler = K2JVMCompiler()
        val compilerArgs = K2JVMCompilerArguments().apply { K2JVMCompiler().parseArguments(args.toTypedArray(), this) }
        if (CompilerArgs.classpath.isNotEmpty())
            compilerArgs.classpath = CompilerArgs.classpath
        else
            compilerArgs.classpath =
                "${CompilerArgs.jvmStdLibPaths.joinToString(
                    postfix = ":",
                    separator = ":"
                )}:${System.getProperty("java.class.path")}"

        compilerArgs.jdkHome = CompilerArgs.jdkHome
        compilerArgs.jvmTarget = CompilerArgs.jvmTarget
        IncrementalCompilation.setIsEnabledForJvm(true)

        val services = Services.EMPTY
        MsgCollector.clear()
        val futureExitCode = threadPool.submit {
            compiler.exec(MsgCollector, services, compilerArgs)
        }
        var hasTimeout = false
        try {
            futureExitCode.get(10L, TimeUnit.SECONDS)
        } catch (ex: TimeoutException) {
            hasTimeout = true
            futureExitCode.cancel(true)
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

    override fun exec(path: String, streamType: Stream): String = commonExec("java -jar $path", streamType)

    private fun analyzeErrorMessage(msg: String): Boolean = !msg.split("\n").any { it.contains(": error:") }

    private val log = Logger.getLogger("compilerErrorsLog")

}