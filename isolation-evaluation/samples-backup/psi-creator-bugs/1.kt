// Original bug: KT-37185
// Duplicated bug: KT-36191

fun test() {
    val x = Test()
    val y = "${x.try}"
}

class Test {
    fun tryA() {}
    fun tryB() {}
}
