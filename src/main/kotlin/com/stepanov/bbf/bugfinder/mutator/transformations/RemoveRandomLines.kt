package com.stepanov.bbf.bugfinder.mutator.transformations

import org.apache.log4j.Logger

class RemoveRandomLines: Transformation()  {

    override val name = "RemoveRandomLines"

    private val log: Logger = Logger.getLogger("mutatorLogger")

    override fun transform() {
        log.debug("RemoveRandomLines mutations")
        val text = file.text.lines().toMutableList()
        for (i in 0..random.nextInt(removeConst)) {
            val numLine = random.nextInt(text.size)
            val old = text[numLine]
            text[numLine] = ""
            if (!checker.checkTextCompiling(getText(text))) {
                text[numLine] = old
            }
        }
        file = psiFactory.createFile(getText(text))
    }

    private fun getText(text: MutableList<String>) = text.joinToString(separator = "\n")

    private val removeConst = file.text.lines().size * 2
}