
class ABC {
    val outerProp: String = TODO()
    fun foo()  {
        open class B {
            val prop = outerProp
        }
        class A : B()
}
}
