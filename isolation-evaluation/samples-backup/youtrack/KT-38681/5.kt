// Original bug: KT-38681
// Duplicated bug: KT-38681

package test

interface IFoo<T> {
    fun foo(x: T, s: String = "K"): String
}

class FooImpl : IFoo<Int> {
    override fun foo(x: Int, s: String): String = x.toString() + s
}

fun main() {
    println(FooImpl().foo(42))
}
