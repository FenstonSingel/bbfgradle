// Original bug: KT-21778
// Duplicated bug: KT-21778

fun main() {
    object {
        fun foo() = x.bar()
        val x = object {
            inline fun bar() = println(1)
        }
    }
}
