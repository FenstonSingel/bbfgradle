package com.stepanov.bbf.bugfinder.mutator.transformations

import com.stepanov.bbf.bugfinder.isolation.ExcessiveMutationException
import org.jetbrains.kotlin.psi.KtExpression
import com.stepanov.bbf.bugfinder.util.getAllPSIChildrenOfType
import com.stepanov.bbf.bugfinder.util.getRandomBoolean
import org.apache.log4j.Logger

class AddNotNullAssertions : Transformation() {

    override val name = "AddNotNullAssertion"

    private val log: Logger = Logger.getLogger("mutatorLogger")

    override fun transform() {
        log.debug("AddNotNullAssertions mutations")
        file.getAllPSIChildrenOfType<KtExpression>()
            .filter { getRandomBoolean(3) }
            .map { tryToAddNotNullAssertion(it) }
    }

    private fun tryToAddNotNullAssertion(exp: KtExpression) {
        try {
            val newExp = psiFactory.createExpressionIfPossible("${exp.text}!!") ?: return
            checker.replacePSINodeIfPossible(file, exp, newExp)
        } catch (e: Exception) {
            if (e is ExcessiveMutationException) throw e
            return
        }
    }
}
