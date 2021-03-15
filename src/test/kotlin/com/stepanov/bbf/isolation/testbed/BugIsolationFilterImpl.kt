package com.stepanov.bbf.isolation.testbed

// TODO tidy things up

//fun isolateBug(sourceFilePath: String): RankedProgramEntities? {
//    val compiler = getCompiler()
//
//    isolationLogger.debug("started isolating $sourceFilePath")
//    isolationLogger.debug("")
//    val ranking: RankedProgramEntities?
//    try {
//        ranking = BugIsolator.isolate("tmp/tmp.kt", compiler)
//        ranking?.let {
//            val sourceFile = File(sourceFilePath)
//            isolationLogger.debug("")
//        }
//    } catch (e: Throwable) {
//        isolationLogger.debug("Exception: ${e.javaClass.name}")
//        isolationLogger.debug(if (e.message.isNullOrEmpty()) "no message" else e.message)
//        return null
//    }
//    isolationLogger.debug("finished isolating $sourceFilePath")
//    isolationLogger.debug("")
//    return ranking
//}

//fun compareIsolationRankings(first: RankedProgramEntities, second: RankedProgramEntities): Double {
//    return first.cosineSimilarity(second)
//}