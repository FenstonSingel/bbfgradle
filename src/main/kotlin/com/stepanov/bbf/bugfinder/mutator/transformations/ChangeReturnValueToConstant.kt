package com.stepanov.bbf.bugfinder.mutator.transformations

import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.KtReturnExpression
import com.stepanov.bbf.bugfinder.executor.MutationChecker
import com.stepanov.bbf.bugfinder.util.getAllChildrenNodes
import com.stepanov.bbf.bugfinder.util.getAllPSIChildrenOfType
import com.stepanov.bbf.bugfinder.util.getRandomBoolean
import org.apache.log4j.Logger

class ChangeReturnValueToConstant : Transformation() {

    override val name = "ChangeReturnValueToConstant"

    private val log: Logger = Logger.getLogger("mutatorLogger")

    override fun transform() {
        log.debug("ChangeReturnValueToConstant mutations")
        val functions = file.getAllPSIChildrenOfType<KtNamedFunction>().filter { getRandomBoolean() }
        for (f in functions) {
            val key = typeConstants.keys.find { f.typeReference?.text?.startsWith(it) == true } ?: continue
            val returns = f.node.getAllChildrenNodes()
                    .asSequence()
                    .filter { it.elementType == KtNodeTypes.RETURN }
                    .map { it.psi as KtReturnExpression }
                    .toList()
            for (r in returns) {
                val replacement = KtPsiFactory(file.project).createExpression(typeConstants[key]!!)
                if (r.returnedExpression != null) {
                    checker.replacePSINodeIfPossible(file, r.returnedExpression!!, replacement)
                }
            }
        }
    }

    //TODO make for a sortedMap, huyap etc.
    private val typeConstants = mapOf(Pair("Int", "1"), Pair("Double", "0.0"), Pair("String", "\"\""),
            Pair("ArrayList", "arrayListOf()"), Pair("List", "listOf()"), Pair("Set", "setOf()"), Pair("Map", "mapOf()"),
            Pair("Array", "arrayOf()"))
}