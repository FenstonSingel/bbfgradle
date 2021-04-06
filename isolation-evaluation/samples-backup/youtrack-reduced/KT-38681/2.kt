
abstract class Base<T> {
open fun foo( a: T,b: Any? = null):Unit = TODO()
}
class Derived : Base<Int>() {
    override fun foo( a: Int,b: Any?) = TODO()
}
fun main() {
Derived().foo(1)
}
