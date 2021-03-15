// Original bug: KT-13306
// Duplicated bug: KT-13306

fun main(args: Array<String>) {
    val p = 1

    (1..2).forEach {
        class A {
            val z = p
        }

        A()
    }
}
