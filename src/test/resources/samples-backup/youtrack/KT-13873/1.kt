// Original bug: KT-36999
// Duplicated bug: KT-13873

class A {
    val component1: () -> String = { "" }
    operator fun component2(): Boolean = true
}

fun test() {
    val a = A()
    val (s, b) = a
}
