package com.stepanov.bbf.bugfinder.isolation.formulas

import com.stepanov.bbf.bugfinder.isolation.RankingFormula
import kotlin.math.sqrt

object OchiaiRankingFormula : RankingFormula {

    override fun calculate(ef: Double, sf: Double, es: Double, ss: Double): Double =
            ef / sqrt((ef + es) * (ef + sf))

    override val isRankDescending = true

}