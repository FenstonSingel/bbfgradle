// Original bug: KT-28228
// Duplicated bug: KT-28228

fun main() {
    B().foo(4)
}

interface A<T> {
    fun foo(x: T, y: Int = 42)
}

class B : A<Int> {
    override fun foo(x: Int, y: Int) {}
}
