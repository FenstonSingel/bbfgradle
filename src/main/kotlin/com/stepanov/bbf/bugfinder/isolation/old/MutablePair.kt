package com.stepanov.bbf.bugfinder.isolation.old

class MutablePair<A, B>(
     var first: A,
     var second: B
) {
    override fun toString(): String = "($first, $second)"
}