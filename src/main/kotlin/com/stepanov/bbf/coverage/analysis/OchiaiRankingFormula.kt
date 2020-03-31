package com.stepanov.bbf.coverage.analysis

import kotlin.math.sqrt

object OchiaiRankingFormula : RankingFormula {

    override fun calculate(cf: Double, cs: Double, uf: Double, us: Double): Double =
        cf / sqrt((cf + cs) * (cf + uf))

    override fun toString(): String =
        "Ochiai metric"

}