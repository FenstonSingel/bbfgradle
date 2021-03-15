// Original bug: KT-31946
// Duplicated bug: KT-38681

// Also reproes on interface
abstract class Base<T> {
    // Also reproes if abstract
    open fun foo(a: T, b: Any? = null) {}
}

// Concrete type HAS to be primitive
class Derived : Base<Int>() {
    override fun foo(a: Int, b: Any?) {}
}

fun main(args: Array<String>) {
    // Must omit argument for parameter with default value
    Derived().foo(42)
}
