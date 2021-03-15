// Original bug: KT-29627
// Duplicated bug: KT-28166

fun <T : UShort> bar(x: T) {}

fun foo() {
    bar(0u)
}
