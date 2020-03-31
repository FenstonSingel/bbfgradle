package com.stepanov.bbf.bugfinder.mutator.transformations

import org.jetbrains.kotlin.psi.KtTypeReference
import com.stepanov.bbf.bugfinder.util.getAllPSIChildrenOfType
import org.apache.log4j.Logger

class AddNullabilityTransformer: Transformation() {

    override val name = "AddNullabilityTransformer"

    private val log: Logger = Logger.getLogger("mutatorLogger")

    override fun transform() {
        log.debug("AddNullability mutations")
        file.getAllPSIChildrenOfType<KtTypeReference>()
                .asSequence()
                .filterNot { it.textContains('?') }
                .map { addNullability(it) }
                .toList()
    }

    private fun addNullability(ref: KtTypeReference) {
        val newRef = psiFactory.createTypeIfPossible("(${ref.typeElement?.text})?") ?: return
        checker.replacePSINodeIfPossible(file, ref, newRef)
    }

}