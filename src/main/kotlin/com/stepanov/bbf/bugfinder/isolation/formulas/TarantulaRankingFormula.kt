package com.stepanov.bbf.bugfinder.isolation.formulas

import com.stepanov.bbf.bugfinder.isolation.RankingFormula

object TarantulaRankingFormula : RankingFormula {

    override fun calculate(ef: Double, sf: Double, es: Double, ss: Double): Double =
            (ef / (ef + sf)) / (ef / (ef + sf) + es / (es + ss))

    override val isRankDescending = true

}