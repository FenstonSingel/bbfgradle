package com.stepanov.bbf.coverage.analysis

object ORankingFormula : RankingFormula {

    override fun calculate(cf: Double, cs: Double, uf: Double, us: Double): Double =
        if (uf > 0) -1.0 else us

    override fun toString(): String =
        "O metric"

}