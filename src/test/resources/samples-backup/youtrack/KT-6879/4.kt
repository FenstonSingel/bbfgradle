// Original bug: KT-11902
// Duplicated bug: KT-6879

class X {
    abstract inner class Y {}
}

fun yy(x: X) = with(x) { object : X.Y() {} }
