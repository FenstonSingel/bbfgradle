
class Cell {
    operator fun get(s: Int) = 1
}
fun box() {
(Cell()[1])++
}
