// Original bug: KT-22270
// Duplicated bug: KT-22270

fun main() {
    a@repeat(3) { i ->
        println("i = $i")
        b@repeat(3) { j ->
            println("j = $j")
            if (j == 1) return@a
        }
    }
    println("Hello World")
}
