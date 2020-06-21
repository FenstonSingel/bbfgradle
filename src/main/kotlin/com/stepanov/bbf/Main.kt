package com.stepanov.bbf

import com.stepanov.bbf.bugfinder.manager.ReportProperties
import org.apache.commons.exec.*
import org.apache.commons.io.FileUtils
import java.io.File
import java.util.*


const val COMMAND = "./gradlew runBBF"
val TIMEOUT_SEC = Properties()
    .also { it.load(File("bbf.conf").inputStream()) }
    .getProperty("BBF_TIMEOUT")?.toLongOrNull() ?: throw IllegalArgumentException("Can't init timeout value")

//data class BBFProcess(val cmd: CommandLine, val file: File,
//                      val handler: DefaultExecuteResultHandler, val executor: DefaultExecutor) {
//    fun execute() {
//        executor.execute(cmd, handler)
//    }
//}
//
//
//fun createBBFProcess(f: File): BBFProcess {
//    val cmd = CommandLine.parse("$COMMAND ${f.absolutePath}")
//    val executor = DefaultExecutor().also {
//        it.watchdog = ExecuteWatchdog(TIMEOUT_SEC * 1000)
//    }
//    val handler = DefaultExecuteResultHandler()
//    return BBFProcess(cmd, f, handler, executor)
//}

const val pathToErrorLogs = "tmp/results/errorLogs"
fun main(args: Array<String>) {
    File("bugsPerMinute.txt").writeText("""
Bugs: 0
Time: 0
Bugs per minute: 0.0  
    """.trimIndent())
    val file = File(pathToErrorLogs)
    if (!file.exists()) file.mkdirs()
    //val dir = File(args[0]).listFiles()
//    val dir = File("tmp/arrays").listFiles()
//    val threads = 1
//    val processes = ArrayList<Pair<BBFProcess, Double>>(threads)
//    repeat(threads){ processes.add(createBBFProcess(dir.random()) to 0.0) }
//    processes.forEach { (pr, _) -> pr.executor.execute(pr.cmd, pr.handler) }
//    val step = 1000
//    while (true) {
//        for ((i, procToTime) in processes.withIndex()) {
//            val (proc, time) = procToTime
//            println("Process $i. Time elapsed: $time Res: ${proc.handler.hasResult()}")
//            if (proc.handler.hasResult()) {
//                processes[i] = createBBFProcess(dir.random()) to 0.0
//                processes[i].first.execute()
//            }
//            processes[i] = proc to time + step
//        }
//        Thread.sleep(1000)
//    }
    args.joinToString(separator = " ")
    var cmdLine = CommandLine.parse(COMMAND)
    var executor = DefaultExecutor().also {
        it.watchdog = ExecuteWatchdog(TIMEOUT_SEC * 1000)
        it.streamHandler = PumpStreamHandler(object : LogOutputStream() {
            override fun processLine(line: String?, level: Int) {
                println(line)
                //executorOutput.add(line)
            }
        })
    }
    var handler = DefaultExecuteResultHandler()
    var timeElapsed = 0
    executor.execute(cmdLine, handler)


    //if (joinedArgs.contains("-f") || joinedArgs.contains("--fuzzing")) {
    var globalCounter = 0L
    while (true) {
        println("Elapsed: $timeElapsed")
        if (handler.hasResult()) {
            if (timeElapsed > TIMEOUT_SEC * 1000) {
                var i = 0
                var newPath: String
                while (true) {
                    newPath = "$pathToErrorLogs/logs$i"
                    if (File(newPath).exists()) i++
                    else break
                }
                val dstDir = File(newPath)
                dstDir.mkdirs()
                File("$dstDir/timeout").writeText("timeout")
                FileUtils.copyDirectory(File("logs"), dstDir)
            }
            //IF error then save logs
            else if (handler.exitValue != 0) {
                var i = 0
                var newPath: String
                while (true) {
                    newPath = "$pathToErrorLogs/logs$i"
                    if (File(newPath).exists()) i++
                    else break
                }
                val dstDir = File(newPath)
                dstDir.mkdirs()
                FileUtils.copyDirectory(File("logs"), dstDir)
            }
            cmdLine = CommandLine.parse(COMMAND)
            handler = DefaultExecuteResultHandler()
            executor = DefaultExecutor().also {
                it.watchdog = ExecuteWatchdog(TIMEOUT_SEC * 1000)
                it.streamHandler = PumpStreamHandler(object : LogOutputStream() {
                    override fun processLine(line: String?, level: Int) {
                        println(line)
                        //executorOutput.add(line)
                    }
                })
            }
            executor.execute(cmdLine, handler)
            timeElapsed = 0
        }
        globalCounter += 1000
        if ((globalCounter / 1000) % 60 == 0L) {
            if (ReportProperties.getPropAsBoolean("SAVE_STATS") == true) saveStats((globalCounter / 1000) / 60)
        }
        timeElapsed += 1000
        Thread.sleep(1000)
    }
    /*} else {
        while (true) {
            if (handler.hasRe sult()) System.exit(0)
        }
    } */
}

private fun saveStats(timeElapsedInMinutes: Long) {
    val f = File("bugsPerMinute.txt")
    val curText = StringBuilder(f.readText())
    val bugs = curText.split("\n").first().split(": ").last().toInt()
    val newText = """
        Bugs: $bugs
        Time: $timeElapsedInMinutes
        Bugs per minute: ${bugs.toDouble() / timeElapsedInMinutes.toDouble()} 
    """.trimIndent()
    f.writeText(newText)
}