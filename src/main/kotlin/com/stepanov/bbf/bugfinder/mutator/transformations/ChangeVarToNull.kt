package com.stepanov.bbf.bugfinder.mutator.transformations

import org.jetbrains.kotlin.psi.KtExpression
import com.stepanov.bbf.bugfinder.util.getAllPSIChildrenOfType
import com.stepanov.bbf.bugfinder.util.getRandomBoolean
import org.apache.log4j.Logger

class ChangeVarToNull : Transformation() {

    override val name = "ChangeVarToNull"

    private val log: Logger = Logger.getLogger("mutatorLogger")

    override fun transform() {
        log.debug("ChangeVarToNull mutations")
        file.getAllPSIChildrenOfType<KtExpression>()
                .filter { random.getRandomBoolean(16) }
                .forEach { checker.replacePSINodeIfPossible(file, it, psiFactory.createExpression("null")) }
    }

}