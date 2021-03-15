// Original bug: KT-29655
// Duplicated bug: KT-29655

abstract class A<T> {
    abstract val value: T
}

data class B(override val value: C) : A<C>()

inline class C(val data: Int)
