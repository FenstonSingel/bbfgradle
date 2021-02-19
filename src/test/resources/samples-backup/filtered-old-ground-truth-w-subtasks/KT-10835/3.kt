// Original bug: KT-10835
// Duplicated bug: KT-10835

class X {
    open inner class Y

    fun foo() {
        with(X()) {
            object : Y() {}
        }
    }
}

fun main(args: Array<String>) {
    X().foo()
}
