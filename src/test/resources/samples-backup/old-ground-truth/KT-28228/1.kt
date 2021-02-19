// Original bug: KT-35970
// Duplicated bug: KT-28228

package test

fun main() {
    (E() as D<Int>).foo(112) // works
    E().foo(112) // crashes compiler
}

interface D<T> {
    fun foo(t: T, a: Int = 1) {
        println("D#foo(t = $t, a = $a)")
    }
}

class E : D<Int> {
    override fun foo(t: Int, a: Int) {
        println("E#foo(t = $t, a = $a)")
    }
}
