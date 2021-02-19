// Original bug: KT-13873
// Duplicated bug: KT-13873

class A

fun foobar(block: (A.() -> Int, A.() -> String, A) -> Unit) { }

fun foo() {
    foobar {
        component1: A.() -> Int,
        component2: A.() -> String,
        x: A ->

        val (a, b) = x
    }
}

