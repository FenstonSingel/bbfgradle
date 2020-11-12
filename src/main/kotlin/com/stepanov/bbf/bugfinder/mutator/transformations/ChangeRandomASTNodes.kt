package com.stepanov.bbf.bugfinder.mutator.transformations

import com.intellij.lang.ASTNode
import org.jetbrains.kotlin.psi.psiUtil.parents
import com.stepanov.bbf.bugfinder.util.getAllChildrenNodes
import com.stepanov.bbf.bugfinder.util.replaceThis
import org.apache.log4j.Logger
import ru.spbstu.kotlin.generate.util.nextInRange

class ChangeRandomASTNodes : Transformation() {

    override val name = "ChangeRandomASTNodes"

    private val log: Logger = Logger.getLogger("mutatorLogger")

    override fun transform() {
        val numOfSwaps = random.nextInRange(numOfSwaps.first, numOfSwaps.second)
        log.debug("ChangeRandomASTNodes mutations: $numOfSwaps swaps")
        for (i in 1 .. numOfSwaps) {
            val children = file.node.getAllChildrenNodes()
            //Swap random nodes
            var randomNode1 = children[random.nextInt(children.size)]
            var randomNode2 = children[random.nextInt(children.size)]
            while (true) {
                if (randomNode1.text.trim().isEmpty() /*|| randomNode1.text.contains("\n")*/
                        || randomNode1.parents().contains(randomNode2))
                    randomNode1 = children[random.nextInt(children.size)]
                else if (randomNode2.text.trim().isEmpty() /*|| randomNode2.text.contains("\n")*/
                        || randomNode2.parents().contains(randomNode1))
                    randomNode2 = children[random.nextInt(children.size)]
                else break
            }
            val new = swap(randomNode1, randomNode2)
            if (!checker.checkTextCompiling(file.text)) {
                swap(new.first, new.second)
            }
        }
    }

    private fun swap(randomNode1: ASTNode, randomNode2: ASTNode): Pair<ASTNode, ASTNode> {
        //var tmp1 = psiFactory.createWhiteSpace()
        //var tmp2 = psiFactory.createWhiteSpace()
        //val tmp3 = tmp1.copy()
        //val tmp4 = tmp2.copy()
        val tmp1 = psiFactory.createProperty("val a = 1")
        val tmp2 = psiFactory.createProperty("val a = 2")
        randomNode1.treeParent.addChild(tmp1.node, randomNode1)
        randomNode2.treeParent.addChild(tmp2.node, randomNode2)
        tmp1.replaceThis(randomNode2.psi)
        tmp2.replaceThis(randomNode1.psi)
        return randomNode2 to randomNode1
    }

    private val numOfSwaps = 10 to 20
}