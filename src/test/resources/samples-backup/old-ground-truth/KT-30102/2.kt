// Original bug: KT-30102
// Duplicated bug: KT-30102

fun main(args: Array<String>) {
    val v = BooleanWrap(true)
}

class BooleanWrap(private val value: Boolean): Comparable<Boolean> by value
