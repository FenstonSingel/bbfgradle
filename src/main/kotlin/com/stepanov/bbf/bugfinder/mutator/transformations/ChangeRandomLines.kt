package com.stepanov.bbf.bugfinder.mutator.transformations

import com.stepanov.bbf.bugfinder.executor.MutationChecker
import org.apache.log4j.Logger
import java.util.*

class ChangeRandomLines : Transformation() {

    override val name = "ChangeRandomLines"

    private val log: Logger = Logger.getLogger("mutatorLogger")

    override fun transform() {
        log.debug("ChangeRandomLines mutations")
        val text = file.text.lines().toMutableList()
        for (i in 0..Random().nextInt(shuffleConst)) {
            val numLine = Random().nextInt(text.size)
            val insLine = Random().nextInt(text.size)
            Collections.swap(text, numLine, insLine)
            if (!checker.checkTextCompiling(getText(text))) {
                Collections.swap(text, numLine, insLine)
            }
        }
        file = psiFactory.createFile(getText(text))
    }

    private fun getText(text: MutableList<String>) = text.joinToString(separator = "\n")

    private val shuffleConst = file.text.lines().size * 4
}