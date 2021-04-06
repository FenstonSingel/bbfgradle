// Original bug: KT-44908
// Duplicated bug: KT-16151

fun f(m: MutableMap<Double, String>) {
    m[0.0] += ""
}
