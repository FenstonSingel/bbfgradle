// Original bug: KT-37716
// Duplicated bug: KT-37716

fun main() {
    bug<Any>()
}

inline fun <reified T> bug() {
    scope {
        val prop = Unit
        object : Bar<T>({ prop }) {}
    }
}

abstract class Bar<T>(f: (Unit) -> Unit)

fun <T> scope(function: () -> Bar<T>): Nothing = TODO()
