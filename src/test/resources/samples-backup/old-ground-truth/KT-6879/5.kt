// Original bug: KT-11902
// Duplicated bug: KT-6879

open class Outer {
    open inner class InnerToBeDer
}

class OuterDerived : Outer() {
    fun fInDerived() {
        open class InnerDerivedOutsideOuter : InnerToBeDer()
        object : InnerDerivedOutsideOuter() { }
    }
}
