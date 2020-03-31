package com.stepanov.bbf.coverage.analysis

object TarantulaRankingFormula : RankingFormula {

    override fun calculate(cf: Double, cs: Double, uf: Double, us: Double): Double =
        (cf / (cf + uf)) / (cf / (cf + uf) + cs / (cs + us))

    override fun toString(): String =
        "Tarantula metric"

}