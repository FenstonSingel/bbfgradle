
class Outer {
    inner abstract class Inner {
        abstract fun foo()
    }
}
fun Outer.test() =
        object : Outer.Inner() {
            override fun foo() {}
        }
