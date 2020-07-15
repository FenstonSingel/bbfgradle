package com.stepanov.bbf.bugfinder.isolation

interface RankingFormula {

    fun calculate(values: EntityExecutionStatistics): Double =
            calculate(
                    values.execsInFails.toDouble(),
                    values.skipsInFails.toDouble(),
                    values.execsInSuccesses.toDouble(),
                    values.skipsInSuccesses.toDouble()
            )

    fun calculate(ef: Double, sf: Double, es: Double, ss: Double): Double

    val isRankDescending: Boolean

}