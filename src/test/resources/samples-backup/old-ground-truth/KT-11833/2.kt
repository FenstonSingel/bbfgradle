// Original bug: KT-11833
// Duplicated bug: KT-11833

class X {
    abstract inner class Y {}
}

fun yy(x: X) = with(x) { object : X.Y() {} }
