package com.stepanov.bbf.bugfinder.isolation

// TODO Implement more formulas.
interface RankingFormula {

    val name: String

    operator fun invoke(values: EntityExecutionStatistics): Double =
            calculate(
                    values.execsInFails.toDouble(),
                    values.skipsInFails.toDouble(),
                    values.execsInSuccesses.toDouble(),
                    values.skipsInSuccesses.toDouble()
            )

    fun calculate(ef: Double, sf: Double, es: Double, ss: Double): Double

    val isRankDescending: Boolean

    // TODO Might be helpful to combine different methods (e.g. O/set intersection/set intersection + other formula)?

}