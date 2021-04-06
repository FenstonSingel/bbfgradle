// Original bug: KT-33740
// Duplicated bug: KT-22274

fun main() {
   foo()
}

fun foo() {
    loop@ listOf(1, 2, 3, 4, 5).forEach {
        if (it == 3) return@loop
        print(it)
    }
}
