// Original bug: KT-14833
// Duplicated bug: KT-14833

import kotlin.reflect.KProperty

var a = 0

object SimpleDelegate {
    operator fun getValue(thisRef: Any?, desc: KProperty<*>): Int {
        return a
    }

    operator fun setValue(thisRef: Any?, desc: KProperty<*>, value: Int) {
        a = value
    }
}

fun box(): String {
    var g by SimpleDelegate

    if (g++ != 0) return "fail g++: $g"
    if (++g != 2) return "fail ++g: $g"
    if (--g != 1) return "fail --g: $g"
    if (g-- != 1) return "fail g--: $g"
    g += 10
    if (g != 10) return "fail g +=: $g"
    g *= 10
    if (g != 100) return "fail g *=: $g"
    g /= 5
    if (g != 20) return "fail g /=: $g"
    g -= 10
    if (g != 10) return "fail g -=: $g"
    g %= 7
    if (g != 3) return "fail g %=: $g"

    return "OK"
}
