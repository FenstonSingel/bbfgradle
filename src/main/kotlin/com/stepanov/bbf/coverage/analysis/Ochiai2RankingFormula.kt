package com.stepanov.bbf.coverage.analysis

import kotlin.math.sqrt

object Ochiai2RankingFormula : RankingFormula {

    override fun calculate(cf: Double, cs: Double, uf: Double, us: Double): Double =
        (cf * us) / sqrt((cf + cs) * (cf + uf) * (us + uf) * (us + cs))

    override fun toString(): String =
        "Ochiai2 metric"

}