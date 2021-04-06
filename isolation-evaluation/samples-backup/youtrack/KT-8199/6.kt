// Original bug: KT-17057
// Duplicated bug: KT-8199

fun foo() {
    val i = 1
    class Test(int: Int = i)
}
