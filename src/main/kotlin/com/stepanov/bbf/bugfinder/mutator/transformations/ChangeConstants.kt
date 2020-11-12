package com.stepanov.bbf.bugfinder.mutator.transformations

import org.jetbrains.kotlin.psi.*
import com.stepanov.bbf.bugfinder.util.getAllPSIChildrenOfType
import com.stepanov.bbf.bugfinder.util.getRandomVariableName
import org.apache.log4j.Logger

class ChangeConstants : Transformation() {

    override val name = "ChangeConstants"

    private val log: Logger = Logger.getLogger("mutatorLogger")

    enum class Type { BOOLEAN, INTEGER, DOUBLE }

    override fun transform() {
        log.debug("ChangeConstants mutations")
        val constants = file.getAllPSIChildrenOfType<KtConstantExpression>()
        val stringConstants = file.getAllPSIChildrenOfType<KtStringTemplateEntry>()
        constants.forEach {
            when {
                it.text == "true" || it.text == "false" -> changeExpression(it,
                    Type.BOOLEAN
                )
                it.textContains('.') -> changeExpression(it,
                    Type.DOUBLE
                )
                else -> changeExpression(it,
                    Type.INTEGER
                )
            }
        }
        //println("constants = ${stringConstants.map { it.text }}")
        stringConstants
                .forEach { changeStringConst(it) }
    }

    private fun changeExpression(exp: KtExpression, type: Type, isRandom: Boolean = true) {
        val replacement = when (type) {
            Type.BOOLEAN -> psiFactory.createExpression("${random.nextBoolean()}")
            Type.DOUBLE -> psiFactory.createExpression("${random.nextDouble()}")
            Type.INTEGER -> psiFactory.createExpression("${random.nextInt()}")
        }
        if (isRandom && random.nextBoolean() || !isRandom)
            checker.replacePSINodeIfPossible(file, exp, replacement)
    }


    private fun changeStringConst(exp: KtStringTemplateEntry, isRandom: Boolean = true) =
            if (isRandom && random.nextBoolean() || !isRandom)
                checker.replacePSINodeIfPossible(
                    file, exp,
                        psiFactory.createExpression(random.getRandomVariableName(NAME_SIZE)))
            else false

    private val NAME_SIZE = 5
}