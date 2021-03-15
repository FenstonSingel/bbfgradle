
interface IFoo<T> {
    fun foo(x: T, s: String = "") 
}
class FooImpl : IFoo<Int> {
    override fun foo( x: Int,s: String)  = TODO()
}
fun main() {
FooImpl().foo(1)
}
