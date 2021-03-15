
tailrec
fun f(i: Int) {
    (1..i).forEach { return f(it) }
}
