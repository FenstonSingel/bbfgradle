package com.stepanov.bbf.bugfinder.mutator.transformations

import com.stepanov.bbf.bugfinder.executor.CompilerArgs
import com.stepanov.bbf.bugfinder.util.NodeCollector
import com.stepanov.bbf.bugfinder.util.getAllChildrenNodes
import com.stepanov.bbf.reduktor.parser.PSICreator
import org.apache.log4j.Logger
import ru.spbstu.kotlin.generate.util.nextInRange
import java.io.File

class ChangeRandomASTNodesFromAnotherTrees : Transformation() {

    override val name = "ChangeRandomASTNodesFromAnotherTrees"

    private val log: Logger = Logger.getLogger("mutatorLogger")

    override fun transform() {
        val randConst = random.nextInRange(numOfTries.first, numOfTries.second)
        val nodes = file.node.getAllChildrenNodes().filter { it.elementType !in NodeCollector.excludes }
        log.debug("ChangeRandomASTNodesFromAnotherTrees mutations: $randConst tries")
        for (i in 1 .. randConst) {
            val randomNode = nodes[random.nextInRange(0, nodes.size - 1)]
            //Searching nodes of same type in another files
            val line = File("database.txt").bufferedReader().lines()
                    .filter { line -> line.takeWhile { it != ' ' } == randomNode.elementType.toString() }.findFirst()
            if (!line.isPresent) continue
            val files = line.get().dropLast(1).takeLastWhile { it != '[' }.split(", ")
            val randomFile =
                    if (files.size == 1)
                        files[0]
                    else
                        files[random.nextInRange(0, files.size - 1)]
            val psi = PSICreator("")
                .getPSIForFile("${CompilerArgs.baseDir}/$randomFile")
            val sameTypeNodes = psi.node.getAllChildrenNodes().filter { it.elementType == randomNode.elementType }
            val targetNode =
                    if (sameTypeNodes.size == 1)
                        sameTypeNodes[0]
                    else
                        sameTypeNodes[random.nextInRange(0, sameTypeNodes.size - 1)]
            checker.replaceNodeIfPossible(file, randomNode, targetNode)
        }
    }

    private val numOfTries = 10 to 20
}