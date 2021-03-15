// Original bug: KT-19861
// Duplicated bug: KT-19861

package test

class C(var string: String)

val c: C? = null

fun test(m: String) {
    c?.string += m
}

fun main(args: Array<String>) {
    test("xx")
}
