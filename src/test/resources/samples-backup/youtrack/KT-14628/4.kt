// Original bug: KT-17109
// Duplicated bug: KT-14628

abstract class Base {
    abstract inner class Inner
}

object Host : Base() {
    object Inner : Base.Inner()
}
