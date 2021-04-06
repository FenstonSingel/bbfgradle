// Original bug: KT-42787
// Duplicated bug: KT-32153

fun <K, V> Map<K, V>.toRecursiveSequence(): Sequence<Map.Entry<K, V>> = sequence {
    suspend fun SequenceScope<Map.Entry<K, V>>.visit(map: Map<K, V>) {
        map.entries.forEach {
            yield(it)
            if (it.value is Map<*, *>) {
                visit(it.value as Map<K, V>)
            }
        }
    }
    visit(this@toRecursiveSequence)
}
