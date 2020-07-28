package com.stepanov.bbf.bugfinder.isolation.formulas

import com.stepanov.bbf.bugfinder.isolation.RankingFormula

object ORankingFormula : RankingFormula {

    override fun calculate(ef: Double, sf: Double, es: Double, ss: Double): Double =
            if (sf > 0) 0.0 else ss

    override val isRankDescending = true

}