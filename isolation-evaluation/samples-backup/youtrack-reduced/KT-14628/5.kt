
open class A {
    open inner class I
}
object O : A() {
    class B : I()
}
