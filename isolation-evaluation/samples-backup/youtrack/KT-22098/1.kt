// Original bug: KT-32563
// Duplicated bug: KT-22098

class C {
    fun String.test() {
        object : F({ foo() }) {}
    }

    fun String.foo(): String = this
}

open class F(
    val f: () -> String
)
