// Original bug: KT-11902
// Duplicated bug: KT-6879

class Outer {
    inner abstract class Inner {
        abstract fun foo()
    }
}

fun Outer.test() =
        object : Outer.Inner() {
            override fun foo() {
                println("yes, ${this@test}!")
            }
        }
