package com.stepanov.bbf.bugfinder.mutator.projectTransformations

import com.intellij.lang.ASTNode
import com.stepanov.bbf.bugfinder.executor.Project
import com.stepanov.bbf.bugfinder.mutator.transformations.ChangeRandomASTNodes
import com.stepanov.bbf.bugfinder.mutator.transformations.Transformation
import com.stepanov.bbf.bugfinder.util.getAllChildrenNodes
import com.stepanov.bbf.bugfinder.util.replaceThis
import com.stepanov.bbf.reduktor.parser.PSICreator
import kotlin.random.Random

class ShuffleNodes : Transformation() {

    override fun transform() {
        val numOfSwaps = Random.nextInt(numOfSwaps.first, numOfSwaps.second)
        val othFiles = checker.otherFiles!!.texts.map { PSICreator("").getPSIForText(it) }
        val files = listOf(file) + othFiles
        for (i in 0 until numOfSwaps) {
            val children = files.flatMap { it.node.getAllChildrenNodes() }
            ChangeRandomASTNodes.swapRandomNodes(children, psiFactory, files)
        }
    }

    val numOfSwaps = 500 to 1000

}