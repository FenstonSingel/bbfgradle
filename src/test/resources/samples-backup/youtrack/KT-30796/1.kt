// Original bug: KT-30796
// Duplicated bug: KT-30796

fun <T> bar(value: T) {
    val x: Any = value ?: 42
}
