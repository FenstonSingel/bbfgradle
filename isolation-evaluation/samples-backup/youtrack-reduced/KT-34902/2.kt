
inline class Foo(val wrapped: Int)
interface Bar {
    val foo: Foo?
}
data class Baz(override val foo: Foo): Bar
