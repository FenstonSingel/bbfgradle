// Original bug: KT-14628
// Duplicated bug: KT-14628

open class A {
    open inner class I
}

object O : A() {
    class B : I()
}
