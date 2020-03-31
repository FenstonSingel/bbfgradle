package com.stepanov.bbf.bugfinder.mutator.transformations

import com.stepanov.bbf.bugfinder.executor.CompilerArgs
import com.stepanov.bbf.bugfinder.util.NodeCollector
import com.stepanov.bbf.bugfinder.util.getAllChildrenNodes
import com.stepanov.bbf.reduktor.parser.PSICreator
import org.apache.log4j.Logger
import java.io.File
import kotlin.random.Random


class ChangeRandomASTNodesFromAnotherTrees : Transformation() {

    override val name = "ChangeRandomASTNodesFromAnotherTrees"

    private val log: Logger = Logger.getLogger("mutatorLogger")

    override fun transform() {
        val randConst = Random.nextInt(numOfTries.first, numOfTries.second)
        val nodes = file.node.getAllChildrenNodes().filter { it.elementType !in NodeCollector.excludes }
        log.debug("ChangeRandomASTNodesFromAnotherTrees mutations: $randConst tries")
        for (i in 1 .. randConst) {
            log.debug("Try №$i of $randConst")
            val randomNode = nodes[Random.nextInt(0, nodes.size - 1)]
            //Searching nodes of same type in another files
            val line = File("database.txt").bufferedReader().lines()
                    .filter { line -> line.takeWhile { it != ' ' } == randomNode.elementType.toString() }.findFirst()
            if (!line.isPresent) continue
            val files = line.get().dropLast(1).takeLastWhile { it != '[' }.split(", ")
            val randomFile =
                    if (files.size == 1)
                        files[0]
                    else
                        files[Random.nextInt(0, files.size - 1)]
            val psi = PSICreator("")
                .getPSIForFile("${CompilerArgs.baseDir}/$randomFile")
            val sameTypeNodes = psi.node.getAllChildrenNodes().filter { it.elementType == randomNode.elementType }
            val targetNode =
                    if (sameTypeNodes.size == 1)
                        sameTypeNodes[0]
                    else
                        sameTypeNodes[Random.nextInt(0, sameTypeNodes.size - 1)]
            checker.replaceNodeIfPossible(file, randomNode, targetNode)
        }
    }

    private val numOfTries = 40 to 50
}