// Original bug: KT-31087
// Duplicated bug: KT-8199

fun foo(value: String) {
    class A(val fail: String = value)
}
