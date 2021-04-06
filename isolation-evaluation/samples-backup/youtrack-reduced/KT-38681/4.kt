
fun main() {
    B().foo(1)
}
interface A<T> {
    fun foo(x: T, y: Int =1)
}
class B : A<Int> {
    override fun foo( x: Int,y: Int) = TODO()
}
