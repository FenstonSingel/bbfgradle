// Original bug: KT-42091
// Duplicated bug: KT-28109

class Cell {
    operator fun get(s: Int) = 1
}

fun box() {
    val c = Cell()
    (c[0])++
}
