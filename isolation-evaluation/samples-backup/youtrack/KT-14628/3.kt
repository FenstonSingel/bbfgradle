// Original bug: KT-34913
// Duplicated bug: KT-14628

// Playground: http://bit.ly/2OoTjXJ

abstract class A() {
    abstract inner class B : A()
}

object Foo : A() {
    object Bar : B()
}
