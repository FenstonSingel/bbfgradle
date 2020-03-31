package com.stepanov.bbf.coverage.analysis

interface RankingFormula {

    fun calculate(cf: Double, cs: Double, uf: Double, us: Double): Double

}