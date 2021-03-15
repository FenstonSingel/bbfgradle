
fun main() {
E().foo(1)
}
interface D<T> {
    fun foo( t: T,a: Int = 1):Unit = TODO()
}
class E : D<Int> {
    override fun foo( t: Int,a: Int) = TODO()
}
