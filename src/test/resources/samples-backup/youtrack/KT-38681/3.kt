// Original bug: KT-30495
// Duplicated bug: KT-38681

open class Test<T> {
    open fun testFun(a: T, b: Boolean = false) { }
}

class SubTest : Test<Boolean>() {
    override fun testFun(a: Boolean, b: Boolean) { super.testFun(a, b) }
}

fun main() {
    SubTest().testFun(true) // -> Code Generation Error
    SubTest().testFun(true, false) // -> Works
}
