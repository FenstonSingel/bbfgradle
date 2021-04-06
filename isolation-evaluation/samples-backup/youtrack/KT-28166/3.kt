// Original bug: KT-28166
// Duplicated bug: KT-28166

inline class Foo(val value: Int)

class Bar {
    fun <F: Foo> F.foo() = "Hello World"
    val foo = Foo(0).foo()      // this line will throw error
}
