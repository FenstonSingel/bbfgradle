
abstract class A {
    abstract inner class B : A()
}
object Foo : A() {
    object Bar : B()
}
