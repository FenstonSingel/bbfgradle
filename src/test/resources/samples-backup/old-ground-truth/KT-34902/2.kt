// Original bug: KT-38748
// Duplicated bug: KT-34902

inline class Foo(val wrapped: Int)

interface Bar {
    val foo: Foo?
}

data class Baz(override val foo: Foo): Bar
