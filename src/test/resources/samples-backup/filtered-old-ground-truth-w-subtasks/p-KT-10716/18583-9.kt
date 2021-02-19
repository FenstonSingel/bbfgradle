// Original bug: KT-18583
// Duplicated bug: KT-18583

sealed class A<L, R> {
    class B<L, R> : A<L, R>()
    class C<L, R> : A<L, R>()

    fun foo(a: A<L, R>) = when (a) {
        is B -> println("B")
        is C -> println("B")
    }
}
