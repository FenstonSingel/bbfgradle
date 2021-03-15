
class Outer {
    open inner class Inner
}
fun main() {
    Outer().run {
        class InnerDerived : Outer.Inner()
    }
}
