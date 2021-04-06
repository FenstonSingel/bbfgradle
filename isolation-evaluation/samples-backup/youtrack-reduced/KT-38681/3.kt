
open class Test<T> {
    open fun testFun( a: T,b: Boolean = false):Unit = TODO()
}
class SubTest : Test<Boolean>() {
    override fun testFun( a: Boolean,b: Boolean) = TODO()
}
fun main() {
    SubTest().testFun(true)
}
