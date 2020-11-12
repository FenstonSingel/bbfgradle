package com.stepanov.bbf.bugfinder.mutator.transformations

import org.jetbrains.kotlin.psi.KtTypeParameter
import com.stepanov.bbf.bugfinder.util.getAllPSIChildrenOfType
import org.apache.log4j.Logger

//TODO maybe add inline keyword to func
//TODO maybe add type params to func?
class AddReifiedToType: Transformation() {

    override val name = "AddReifiedToType"

    private val log: Logger = Logger.getLogger("mutatorLogger")

    override fun transform() {
        log.debug("AddReifiedToType mutations")
        val typeParameters = file.getAllPSIChildrenOfType<KtTypeParameter>()
        typeParameters.forEach {
            val newTypeModifier = psiFactory.createTypeParameter("reified ${it.text}")
            checker.replacePSINodeIfPossible(file, it, newTypeModifier)
        }
    }

}