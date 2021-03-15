// Original bug: KT-24805
// Duplicated bug: KT-24805

inline fun foo() = false

fun run(x: Boolean, y: Boolean): String {
    var i = 10
    l1@ l2@ do {
        i += 1
        if (i > 100) return "NOT_OK"
        if (y) continue@l2
        if (x) continue@l1
    } while(foo())

    return "OK"
}

fun main(args: Array<String>) {
    println(run(true, true))
}
