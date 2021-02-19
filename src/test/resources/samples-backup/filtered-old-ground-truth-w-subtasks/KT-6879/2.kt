// Original bug: KT-11902
// Duplicated bug: KT-6879

class A {
    open inner class AB
}

fun A.foo() {
    class FooC : A.AB()
}

fun main(args: Array<String>) {
    A().foo()
}
