// Original bug: KT-27512
// Duplicated bug: KT-18583

sealed class A<T> {
    class B : A<Unit>()

    fun foo() = when (this) {
        is B -> println("B")
    }
}
