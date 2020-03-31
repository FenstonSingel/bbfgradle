package com.stepanov.bbf.bugfinder.mutator.transformations

import com.stepanov.bbf.bugfinder.executor.Checker
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPsiFactory

abstract class Transformation {

    abstract fun transform()

    val psiFactory = KtPsiFactory(file.project)

    abstract val name: String

    companion object {
        lateinit var file: KtFile
        lateinit var checker: Checker
        lateinit var currentMutation: String
    }

}