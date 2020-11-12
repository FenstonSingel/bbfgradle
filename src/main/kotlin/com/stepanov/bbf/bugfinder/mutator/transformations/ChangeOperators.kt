package com.stepanov.bbf.bugfinder.mutator.transformations

import com.intellij.lang.ASTNode
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.lexer.KtTokens
import com.stepanov.bbf.bugfinder.util.getAllChildrenNodes
import com.stepanov.bbf.bugfinder.util.getRandomBoolean
import org.apache.log4j.Logger

class ChangeOperators : Transformation() {

    override val name = "ChangeOperators"

    private val log: Logger = Logger.getLogger("mutatorLogger")

    override fun transform() {
        log.debug("ChangeOperators mutations")
        val operators = file.node.getAllChildrenNodes()
                .filter { it.treeParent.elementType == KtNodeTypes.OPERATION_REFERENCE || it.elementType == KtTokens.DOT }
        operators.forEach {
            when (it.text) {
                "<" -> replaceOperator(it.treeParent, listOf(">=", ">", "<=", "==", "!="))
                "<=" -> replaceOperator(it.treeParent, listOf(">=", "<", ">", "==", "!="))
                ">" -> replaceOperator(it.treeParent, listOf(">=", "<", "<=", "==", "!="))
                ">=" -> replaceOperator(it.treeParent, listOf(">", "<", "<=", "==", "!="))
                "==" -> replaceOperator(it.treeParent, "!=")
                "!=" -> replaceOperator(it.treeParent, "==")
                "+" -> replaceOperator(it.treeParent, listOf("-", "*", "/", "%"))
                "-" -> replaceOperator(it.treeParent, listOf("+", "*", "/", "%"))
                "/" -> replaceOperator(it.treeParent, listOf("-", "*", "+", "%"))
                "*" -> replaceOperator(it.treeParent, listOf("-", "+", "/", "%"))
                //TODO add ?.
                "." -> replaceOperator(it, "!!.")
                "+=" -> replaceOperator(it.treeParent, listOf("-=", "*=", "/="))
                "-=" -> replaceOperator(it.treeParent, listOf("+=", "*=", "/="))
                "*=" -> replaceOperator(it.treeParent, listOf("-=", "+=", "/="))
                "/=" -> replaceOperator(it.treeParent, listOf("-=", "*=", "+="))
                "&&" -> replaceOperator(it.treeParent, "||")
                "||" -> replaceOperator(it.treeParent, "&&")
                "++" -> replaceOperator(it, "--")
                "--" -> replaceOperator(it, "++")
                else -> return@forEach
            }
        }
    }

    private fun replaceOperator(replace: ASTNode, replacement: List<String>, isRandom: Boolean = true) {
        if (isRandom && random.getRandomBoolean() || !isRandom) {
            val index = random.nextInt(replacement.size)
            replaceOperator(replace, replacement[index])
        }
    }

    private fun replaceOperator(replace: ASTNode, replacement: String, isRandom: Boolean = true) {
        if (isRandom && random.getRandomBoolean() || !isRandom) {
            val replacementNode =
                    if (replace.elementType == KtNodeTypes.OPERATION_REFERENCE)
                        psiFactory.createOperationName(replacement)
                    else
                        psiFactory.createExpression(replacement)

            checker.replacePSINodeIfPossible(file, replace.psi, replacementNode)
        }
    }


}