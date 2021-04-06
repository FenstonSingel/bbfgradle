// Original bug: KT-29754
// Duplicated bug: KT-19861

class S(var x: String)

var S?.y: String
    get() = this?.x ?: ""
    set(value) {
        if (this != null) this.x = value
    }

fun test(s: S?) {
    s?.y += "abc"
}
