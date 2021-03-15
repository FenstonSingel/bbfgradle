// Original bug: KT-29181
// Duplicated bug: KT-29181

fun main() {
    print(test())
}

fun test(): Int {
    var ints = arrayOf(3, 4, 5, 6)
    var x = lit@ ints.forEach {
        if (it == 3) return@lit 10
        println(it)
    }
    return 100
}
