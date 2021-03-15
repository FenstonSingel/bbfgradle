
inline class Foo(val value: Int)
fun <F: Foo> F.foo() = ""
    val foo = Foo(1).foo()
