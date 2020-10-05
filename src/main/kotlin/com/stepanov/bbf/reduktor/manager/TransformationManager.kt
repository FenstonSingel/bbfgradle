package com.stepanov.bbf.reduktor.manager

import com.intellij.psi.PsiWhiteSpace
import com.stepanov.bbf.reduktor.executor.CommonCompilerCrashTestChecker
import com.stepanov.bbf.reduktor.executor.CompilerArgs
import com.stepanov.bbf.reduktor.executor.CompilerTestChecker
import com.stepanov.bbf.reduktor.executor.backends.CommonBackend
import com.stepanov.bbf.reduktor.executor.error.Error
import com.stepanov.bbf.reduktor.executor.error.ErrorType
import com.stepanov.bbf.reduktor.parser.PSICreator
import com.stepanov.bbf.reduktor.passes.*
import com.stepanov.bbf.reduktor.passes.slicer.Slicer
import com.stepanov.bbf.reduktor.util.ReduKtorProperties
import com.stepanov.bbf.reduktor.util.TaskType
import com.stepanov.bbf.reduktor.util.getAllChildrenNodes
import com.stepanov.bbf.reduktor.util.startTasksAndSaveNewFiles
import org.apache.log4j.Logger
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.resolve.BindingContext
import java.io.File
import java.io.PrintWriter

class TransformationManager(private val ktFiles: List<KtFile>) {

    var ktFactory: KtPsiFactory? = null
    var context: BindingContext? = null
    private val log = Logger.getLogger("transformationManagerLog")

    init {
        ktFactory = KtPsiFactory(ktFiles[0].project)
    }

    fun doProjectTransformations(targetFiles: List<KtFile>, creator: PSICreator, backend: CommonBackend) {
        val projectDir = CompilerArgs.projectDir
        //TODO Peephole passes for java files?
        val checker = CommonCompilerCrashTestChecker(backend)
        val errorInfo = checker.init(projectDir, KtPsiFactory(targetFiles[0]))
        println("error = $errorInfo")
        println("FILE = ${Error.pathToFile}")
        if (errorInfo.type == ErrorType.UNKNOWN)
            System.exit(0)
        var file = targetFiles.find { it.name == Error.pathToFile }!!
        if (file.name == Error.pathToFile) {
            startTasksAndSaveNewFiles(
                creator.targetFiles,
                projectDir,
                TaskType.SIMPLIFYING,
                backend
            )
            creator.reinit(projectDir)
            checker.reinit()
            file = targetFiles.find { it.name == Error.pathToFile }!!
            PreliminarySimplification(file, projectDir, backend).computeSlice(creator.targetFiles)
            creator.reinit(projectDir)
//            checker.reinit()
//            file = creator.targetFiles.find { it.name == Error.pathToFile }!!
//            Slicer(file, checker).computeSlice(errorInfo.line, Slicer.Level.INTRAPROCEDURAL)
//            Slicer(file, checker).computeSlice(errorInfo.line, Slicer.Level.FUNCTION)
//            Slicer(file, checker).computeSlice(errorInfo.line, Slicer.Level.CLASS)
//            Save new file with error and reinit
            val newFile = File(file.name)
            val writer = newFile.bufferedWriter()
            writer.write(file.text)
            writer.close()
            checker.reinit()
//            file = creator.targetFiles.find { it.name == Error.pathToFile }!!
//            RemoveInheritance(file, checker).transform(creator.targetFiles)
//            SimplifyInheritance(file, checker).transform(creator.targetFiles)
//            creator.reinit(debugProjectDir)
//            CCTC.reinit()
            //Now transform file with bug
            file = creator.targetFiles.find { it.name == Error.pathToFile }!!
            val TM = TransformationManager(listOf(file))
            val res = TM.doTransformationsForFile(file, checker, true, projectDir)
            println("Res = ${res.text}")
        }
        //Saving
//        for (f in newFiles) {
//            println("SAVING ${f.name}")
//            val directory = pathToSave + f.name.substring(debugProjectDir.length, f.name.indexOfLast { it == '/' })
//            val path = pathToSave + f.name.substring(debugProjectDir.length)
//            val newDirs = File(directory)
//            newDirs.mkdirs()
//            val newFile = File(path)
//            val writer = newFile.bufferedWriter()
//            writer.write(f.text)
//            writer.close()
//        }
    }


    fun doTransformationsForFile(file: KtFile, checker: CompilerTestChecker,
                                 isProject: Boolean = false, projectDir: String = ""): KtFile {
        log.debug("FILE NAME = ${file.name}")
        file.beforeAstChange()
        val pathToSave = StringBuilder(file.name)
        pathToSave.insert(pathToSave.indexOfLast { it == '/' }, "/minimized")
        checker.pathToFile = file.name
        require(checker.checkTest(file.text)) { "No bug" }
        var rFile = file.copy() as KtFile
        try {
            if (isProject)
                checker.init(projectDir, ktFactory!!)
            else
                checker.init(file.name, ktFactory!!)
        } catch (e: IllegalArgumentException) {
            return rFile
        }
        checker.refreshAlreadyCheckedConfigurations()
//        if (checker.getErrorMessage().contains("Unresolved") || checker.getErrorMessage().contains("Expecting")
//                || checker.getErrorMessage().isEmpty() || checker.getErrorInfo().type == ErrorType.UNKNOWN) {
//            return file
//        }
        log.debug("ERROR = ${checker.getErrorInfo()}")
        //log.debug("MSG = ${CCTC.getErrorInfo().errorMessage}")
//            if (CCTC.getErrorInfo().type == ErrorType.UNKNOWN)
//                continue
        var oldRes = file.text
//            DeleteComments(CCTC).transform(rFile)

        fun performTransformation(name: String, action: () -> Unit) {
            if (Thread.interrupted()) throw InterruptedException()
            action()
            rFile = KtPsiFactory(rFile.project).createFile(rFile.name, rFile.text)
            log.debug("VERIFY $name = ${checker.checkTest(rFile.text)}")
            log.debug("CHANGES AFTER $name ${rFile.text != oldRes}")
        }

        fun performTransformations(list: List<Pair<String, () -> Unit>>) {
            for ((name, action) in list) {
                performTransformation(name, action)
            }
        }

        while (true) {
            var isInterrupted = false
            try {
                if (ReduKtorProperties.getPropAsBoolean("TRANSFORMATIONS") == true) {
                    val commonTransformations = listOf(
                        "RemoveSuperTypeList" to { RemoveSuperTypeList(rFile, checker).transform() },
                        "SimplifyControlExpression" to { SimplifyControlExpression(rFile, checker).transform() },
                        "SimplifyFunAndProp" to { SimplifyFunAndProp(rFile, checker).transform() },
                        "ReplaceBlockExpressionToBody" to { ReplaceBlockExpressionToBody(rFile, checker).transform() },
                        "SimplifyWhen" to { SimplifyWhen(rFile, checker).transform() },
                        "ReturnValueToVoid" to { ReturnValueToVoid(rFile, checker).transform() },
                        "ElvisOperatorSimplifier" to { ElvisOperatorSimplifier(rFile, checker).transform() },
                        "TryCatchDeleter" to { TryCatchDeleter(rFile, checker).transform() },
                        "SimplifyIf" to { SimplifyIf(rFile, checker).transform() },
                        "SimplifyFor" to { SimplifyFor(rFile, checker).transform() },
                        "RemoveInheritance" to { RemoveInheritance(rFile, checker).transform() },
                        "SimplifyInheritance" to { SimplifyInheritance(rFile, checker).transform() },
                        "SimplifyConstructor" to { SimplifyConstructor(rFile, checker).transform() },
                        "ReturnValueToConstant" to { ReturnValueToConstant(rFile, checker).transform() },
                        "SimplifyBlockExpression" to { SimplifyBlockExpression(rFile, checker).transform() },
                        "SimplifyStringConstants" to { SimplifyStringConstants(rFile, checker).transform() },
                        "RemoveParameterFromDeclaration" to {
                            RemoveParameterFromDeclaration(rFile, checker).transform()
                        },
                        "FunInliner" to { FunInliner(rFile, checker).transform() },
                        "ReplaceArgOnTODO" to { ReplaceArgOnTODO(rFile, checker).transform() },
                        "ConstructionsDeleter" to { ConstructionsDeleter(rFile, checker).transform() },
                        "EqualityMapper" to { EqualityMapper(rFile, checker).transform() }
                    )
                    performTransformations(commonTransformations)

                    File(rFile.name).writeText(rFile.text)
                    val creator = PSICreator("")
                    rFile = creator.getPSIForFile(rFile.name, true)
                    if (creator.ctx != null) {
                        performTransformation("MinorSimplifyings") {
                            MinorSimplifyings(rFile, checker, creator.ctx!!).transform()
                        }
                    }

                    if (Thread.interrupted()) throw InterruptedException()
                    val newText = PeepholePasses(rFile.text, checker, false).transform()
                    log.debug("CHANGES AFTER PEEPHOLE ${newText != oldRes}")
                    log.debug("VERIFY PEEPHOLE = ${checker.checkTest(newText)}")
                    rFile = KtPsiFactory(rFile.project).createFile(rFile.name, newText)
                    log.debug("UPDATED ${checker.checkTest(rFile.text)}")
                }
                if (ReduKtorProperties.getPropAsBoolean("SLICING") == true) {
                    var errorInfo = checker.getErrorInfo()
                    log.debug("ERROR INFO = $errorInfo")
                    val slicingTransformations = listOf(
                        "INTRAPROCEDURAL" to {
                            Slicer(rFile, checker).computeSlice(errorInfo.line, Slicer.Level.INTRAPROCEDURAL)
                        },
                        "FUNCTION" to {
                            errorInfo = checker.reinit()
                            Slicer(rFile, checker).computeSlice(errorInfo.line, Slicer.Level.FUNCTION)
                        },
                        "CLASS" to {
                            errorInfo = checker.reinit()
                            Slicer(rFile, checker).computeSlice(errorInfo.line, Slicer.Level.CLASS)
                        }
                    )
                    performTransformations(slicingTransformations)
                }
                if (ReduKtorProperties.getPropAsBoolean("FASTREDUCE") == true) {
                    performTransformation("FASTREDUCE") { PSIReducer(rFile, checker).transform() }
                }
                if (ReduKtorProperties.getPropAsBoolean("HDD") == true) {
                    performTransformation("HDD") { HierarchicalDeltaDebugger(rFile.node, checker).hdd() }
                }
            } catch (e: InterruptedException) {
                isInterrupted = true
            } finally {
                RemoveWhitespaces(rFile, checker).transform()
                rFile = KtPsiFactory(rFile.project).createFile(rFile.name, rFile.text)
                log.debug("CURRENT RESULT = ${rFile.text}")
                if (rFile.text.filterNot { it.isWhitespace() } == oldRes.filterNot { it.isWhitespace() }) {
                    //if (rFile.text.filterNot { it == '\n' } == oldRes.filterNot { it == '\n' }) {
                    break
                }
                oldRes = rFile.text
                if (isInterrupted) break
            }
        }
        log.debug("VERIFY = ${checker.checkTest(rFile.text)}")
        log.debug("RESULT: ${rFile.text}")
//      SAVING
        if (!isProject && ReduKtorProperties.getPropAsBoolean("SAVE_RESULT") == true) {
            File(pathToSave.toString().substringBeforeLast('/')).mkdirs()
            val writer = PrintWriter(pathToSave.toString())
            writer.print(rFile.text)
            writer.close()
        }
        val tokens = rFile.node.getAllChildrenNodes().filter { it.psi !is PsiWhiteSpace }.size
        tokensSum += tokens
        log.debug("TOKENS = $tokens ")
        return rFile
    }

    var tokensSum: Long = 0

    fun doTransformations(checker: CompilerTestChecker, isProject: Boolean = false, projectDir: String = "") {
        println("Size = ${ktFiles.size}")
        for (file in ktFiles) {
            doTransformationsForFile(file, checker, isProject, projectDir)
//            //Saving
//            val pathToSave = StringBuilder(file.name)
//            pathToSave.insert(pathToSave.indexOfLast { it == '/' }, "/minimized")
//            File(pathToSave.substring(0, pathToSave.indexOfLast { it == '/' })).mkdirs()
//            val writer = PrintWriter(pathToSave.toString())
//            writer.print(reducedFile.text)
//            writer.close()
        }
    }


    fun doForParallelSimpleTransformations(isProject: Boolean = false, projectDir: String = "", backend: CommonBackend): KtFile? {
        //Temporary
        for ((i, file) in ktFiles.withIndex()) {
            log.debug("FILE NAME = ${file.name}")
            log.debug("FILE NUM = $i")
            file.beforeAstChange()
            val pathToSave = StringBuilder(file.name)
            pathToSave.insert(pathToSave.indexOfLast { it == '/' }, "/minimized")
            val CCTC = CommonCompilerCrashTestChecker(backend)
            var rFile = file.copy() as KtFile
            CCTC.pathToFile = rFile.name
            log.debug("proj = ${projectDir}")
            if (isProject) {
                println("PROJ = $projectDir isProj = $isProject File = ${file.name}")
                CCTC.init(projectDir, ktFactory!!)
            } else
                CCTC.init(file.name, ktFactory!!)
            CCTC.refreshAlreadyCheckedConfigurations()
            log.debug("ERROR = ${CCTC.getErrorInfo()}")
//            if (CommonCompilerCrashTestChecker.getErrorInfo().type == ErrorType.UNKNOWN)
//                continue
            CCTC.pathToFile = rFile.name
            SimplifyFunAndProp(rFile, CCTC).transform()
            val newText = PeepholePasses(rFile.text, CCTC, true).transform()
            rFile = KtPsiFactory(rFile.project).createFile(rFile.name, newText)
            ConstructionsDeleter(rFile, CCTC).transform()
            //X3
//            RemoveParameterFromDeclaration(rFile, CCTC, files).transform()
            //RemoveWhitespaces(rFile, CCTC).transform()
            RemoveUnusedImports(rFile, CCTC).transform()
            log.debug("VERIFY = ${CCTC.checkTest(rFile.text, rFile.name)}")
            return rFile
        }
        return null
    }
}
