// Original bug: KT-17018
// Duplicated bug: KT-17018

fun <T> foo(block: () -> T) {}
fun bar()= Any()

fun test() {
    foo<() -> Any> {
        when (1) {
            1 -> ::bar
            else -> throw IllegalStateException()
        }
    }
}
