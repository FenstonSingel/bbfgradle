package com.stepanov.bbf.bugfinder.mutator.transformations

import org.jetbrains.kotlin.psi.*
import com.stepanov.bbf.bugfinder.util.getAllPSIChildrenOfType
import com.stepanov.bbf.bugfinder.util.getRandomVariableName
import org.apache.log4j.Logger

class AddBlockToExpression : Transformation() {

    override val name = "AddBlockToExpression"

    private val log: Logger = Logger.getLogger("mutatorLogger")

    override fun transform() {
        log.debug("AddBlockToExpression mutations")
        val expr = file.getAllPSIChildrenOfType<KtExpression>()
        expr.forEach {
            generateRandomBooleanExpression(it)?.let { blockExpr ->
                checker.replacePSINodeIfPossible(file, it, blockExpr)
            }
        }
    }

    private fun generateRandomBooleanExpression(exp: KtExpression): KtBlockExpression? {
        val varList = mutableListOf<String>()
        val names = mutableListOf<String>()
        val logicalOps = listOf("&&", "||")
        repeat(random.nextInt(randomConst) + 1) {
            val name = random.getRandomVariableName(randomConst)
            val value = random.nextBoolean()
            names.add(name)
            varList.add("val $name = $value")
        }
        val expr = StringBuilder()
        names.forEach {
            if (it != names.last())
                expr.append("$it ${logicalOps[random.nextInt(2)]} ")
            else
                expr.append(it)
        }
        return try {
            val res = when (random.nextInt(3)) {
                0 -> psiFactory.createExpression("if (${expr}) {${exp.text}} else {${exp.text}}") as KtIfExpression
                1 -> psiFactory.createExpression("when (${expr}) {\n true -> {${exp.text}}\n else -> {${exp.text}}\n}") as KtWhenExpression
                else -> psiFactory.createExpression("try\n{${exp.text}}\ncatch(e: Exception){}\nfinally{}") as KtTryExpression
            }
            val block = psiFactory.createBlock(varList.joinToString("\n") + "\n${res.text}")
            //Remove braces
            block.deleteChildInternal(block.lBrace!!.node)
            block.deleteChildInternal(block.rBrace!!.node)
            block
        } catch (e: Exception) {
            null
        }
    }

    //!!!!
    private val randomConst = 1
}