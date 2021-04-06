// Original bug: KT-22000
// Duplicated bug: KT-10835

class Outer {
    open inner class Inner
}

fun main(args: Array<String>) {
    Outer().run {
        class InnerDerived : Outer.Inner()
    }
}
