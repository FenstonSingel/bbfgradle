// Original bug: KT-13964
// Duplicated bug: KT-13964

data class Wrapper<T>(val value: T) {
    operator fun <R> invoke(other: R) where R : T { }
}

fun main(args: Array<String>) {
    val w = Wrapper("abc")
    w(1)
}
