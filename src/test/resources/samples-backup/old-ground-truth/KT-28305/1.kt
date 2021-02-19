// Original bug: KT-28305
// Duplicated bug: KT-28305

fun <T> foo(list: List<T>) {
    val x: Any = list[42] ?: return
}
