// Original bug: KT-21092
// Duplicated bug: KT-21092

class A<T>(val b: T) {}

fun test() {
    val a = { A(1).b::javaClass }
}
