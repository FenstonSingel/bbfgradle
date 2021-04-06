package com.stepanov.bbf.bugfinder.mutator.transformations

import com.stepanov.bbf.bugfinder.util.getAllChildrenNodes
import com.stepanov.bbf.bugfinder.util.removeArbitraryChild
import com.stepanov.bbf.reduktor.util.replaceThis
import org.apache.log4j.Logger
import ru.spbstu.kotlin.generate.util.nextInRange

class RemoveRandomASTNodes : Transformation() {

    override val name = "RemoveRandomASTNodes"

    private val log: Logger = Logger.getLogger("mutatorLogger")

    override fun transform() {
        val numOfSwaps = random.nextInRange(numOfSwaps.first, numOfSwaps.second)
        log.debug("RemoveRandomASTNodes mutations: $numOfSwaps swaps")
        for (i in 1 .. numOfSwaps) {
            val children = file.node.getAllChildrenNodes()
            val randomNode = children[random.nextInt(children.size)]
            checker.removeNodeIfPossible(file, randomNode)
        }
    }

    private val numOfSwaps = 400 to 600

}