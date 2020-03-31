package com.stepanov.bbf.coverage.data

enum class SegmentType {
    INSTRUCTIONS,
    BRANCHES,
    LINES,
    PATHS,
    METHODS,
    CLASSES;

    override fun toString(): String =
        when (this) {
            INSTRUCTIONS -> "instructions"
            BRANCHES -> "branches"
            LINES -> "lines"
            PATHS -> "paths"
            METHODS -> "methods"
            CLASSES -> "classes"
        }

}