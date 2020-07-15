package com.stepanov.bbf.bugfinder.isolation.formulas

import com.stepanov.bbf.bugfinder.isolation.RankingFormula
import kotlin.math.sqrt

object Ochiai2RankingFormula : RankingFormula {

    override fun calculate(ef: Double, sf: Double, es: Double, ss: Double): Double {
        val result = (ef * ss) / sqrt((ef + es) * (ef + sf) * (ss + sf) * (ss + es))
        // TODO Think a bit more on how to handle NaNs. Maybe try switching to more stable helper formula?
        return if (result.isNaN()) -1.0 else result
    }

    override val isRankDescending = true

}