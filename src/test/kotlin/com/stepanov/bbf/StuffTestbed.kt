package com.stepanov.bbf

import com.stepanov.bbf.bugfinder.Reducer
import com.stepanov.bbf.bugfinder.executor.MultiCompilerCrashChecker
import com.stepanov.bbf.bugfinder.executor.WitnessTestsCollector
import com.stepanov.bbf.bugfinder.executor.compilers.JVMCompiler
import com.stepanov.bbf.bugfinder.mutator.transformations.RemoveRandomASTNodes
import com.stepanov.bbf.bugfinder.mutator.transformations.Transformation
import com.stepanov.bbf.reduktor.parser.PSICreator
import com.stepanov.bbf.reduktor.passes.FunInliner
import org.apache.log4j.PropertyConfigurator

fun main() {

    PropertyConfigurator.configure("src/main/resources/bbfLog4j.properties")
    PropertyConfigurator.configure("src/main/resources/reduktorLog4j.properties")

////    val oldFile = PSICreator("").getPSIForFile("/home/ruban/kotlin-samples/ground-truth/KT-32153/5.kt")
////    val newFile = Reducer.reduce("/home/ruban/kotlin-samples/ground-truth/KT-32153/5.kt", JVMCompiler())
//
//    val creator = PSICreator("")
//    val file = creator.getPSIForFile("/home/ruban/kotlin-samples/ground-truth/KT-32153/5.kt")
//    Transformation.file = file
//    val checker = MultiCompilerCrashChecker(JVMCompiler())
//    checker.pathToFile = file.name
//
//    FunInliner(file, checker).transform()
//    1 + 1

    val creator = PSICreator("")
    val file = creator.getPSIForFile("/home/ruban/kotlin-samples/filtered-ground-truth-w-subtasks-reduced/KT-10835/1.kt")
    Transformation.file = file
    val checker = WitnessTestsCollector(JVMCompiler())
    Transformation.checker = checker

    Transformation.currentMutation = RemoveRandomASTNodes().name
    RemoveRandomASTNodes().transform()

}