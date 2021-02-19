// Original bug: KT-11833
// Duplicated bug: KT-11833

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
