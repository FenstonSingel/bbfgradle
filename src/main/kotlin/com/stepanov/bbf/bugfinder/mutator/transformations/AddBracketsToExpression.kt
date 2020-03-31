package com.stepanov.bbf.bugfinder.mutator.transformations

import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtWhenExpression
import com.stepanov.bbf.bugfinder.util.getAllPSIChildrenOfType
import com.stepanov.bbf.bugfinder.util.getRandomBoolean
import org.apache.log4j.Logger

class AddBracketsToExpression : Transformation() {

    override val name = "AddBracketsToExpression"

    private val log: Logger = Logger.getLogger("mutatorLogger")

    override fun transform() {
        log.debug("AddBracketsToExpression mutations")
        file.getAllPSIChildrenOfType<KtExpression>().filter {
            getRandomBoolean(4)
        }.forEach {
            //KOSTYL'!!!!!!
            if (it is KtWhenExpression) return@forEach

            val newExpr = psiFactory.createExpression("(${it.text})")
            checker.replacePSINodeIfPossible(file, it, newExpr)
        }
    }
}