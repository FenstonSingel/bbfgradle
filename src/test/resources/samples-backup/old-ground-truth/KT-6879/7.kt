// Original bug: KT-17626
// Duplicated bug: KT-6879

class Outer {
    open inner class Inner

    fun test() {
        open class Local1 : Inner()
        class Local2 : Local1()
    }
}
