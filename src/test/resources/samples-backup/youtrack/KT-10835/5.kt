// Original bug: KT-10835
// Duplicated bug: KT-10835

fun <T, R> with2(receiver: T, block: T.() -> R): R {    
    return receiver.block()
}


class X {
    open inner class Y

    fun foo() {
        with2(X()) {
            object : Y() {}
        }
    }
}

fun main(args: Array<String>) {
    X().foo()
}
