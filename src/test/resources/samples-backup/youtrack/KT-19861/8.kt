// Original bug: KT-29754
// Duplicated bug: KT-19861

package test

class R(var x: String)

fun test(r: R?) {
    r?.x += "123"
}
