// Original bug: KT-18307
// Duplicated bug: KT-14961

tailrec
fun f(i: Int) {
    (1..i-1).forEach { return f(it) }
}

fun main(args: Array<String>) {
    f(10)
}
