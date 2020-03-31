package com.stepanov.bbf.coverage.data

enum class EntityType {
    LINES,
    METHODS,
    CLASSES,
    SOURCE_FILES;

    override fun toString(): String =
        when (this) {
            LINES -> "line"
            METHODS -> "method"
            CLASSES -> "class"
            SOURCE_FILES -> "source file"
        }

}