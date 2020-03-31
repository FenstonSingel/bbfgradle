package com.stepanov.bbf.coverage.analysis

enum class CoverageType {
    FAILED,
    PASSED;

    override fun toString(): String =
        when (this) {
            CoverageType.FAILED -> "failed"
            CoverageType.PASSED -> "passed"
        }

}