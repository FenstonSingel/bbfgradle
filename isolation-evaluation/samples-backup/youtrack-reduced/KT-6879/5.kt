
open class Outer {
    open inner class InnerToBeDer
}
class OuterDerived : Outer() {
    fun fInDerived() {
        open class InnerDerivedOutsideOuter : InnerToBeDer()
        object : InnerDerivedOutsideOuter() {}
    }
}
