package com.stepanov.bbf.bugfinder.isolation.formulas

import com.stepanov.bbf.bugfinder.isolation.RankingFormula
import kotlin.math.sqrt

object Ochiai2RankingFormula : RankingFormula {

    override fun calculate(ef: Double, sf: Double, es: Double, ss: Double): Double {
        val result = (ef * ss) / sqrt((ef + es) * (ef + sf) * (ss + sf) * (ss + es))
        /* This formula has some potential troubles with division by zero.
         * However, the only way for a zero to appear as a denominator is if ss + sf == 0.
         * ef can't be zero by construction, so ef + es != 0 and ef + sf != 0.
         * ss + es can't be zero because the entity can't be neither executed nor skipped.
         * Therefore, the only possibility left is if ss + sf == 0.
         * (This is also why getting an +-INF instead of NaN is impossible:
         * ss + sf == 0 => ss == 0, and thus we get 0 / 0)
         * Such a situation means that the entity is always being executed,
         * and this is why such entities are deemed uninteresting. */
        return if (result.isNaN()) 0.0 else result
    }

    override val isRankDescending = true

}