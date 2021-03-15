
fun <K, V> Map<K, V>.toRecursiveSequence(): Sequence<Map.Entry<K, V>> = sequence {
    suspend fun SequenceScope<Map.Entry<K, V>>.visit(map: Map<K, V>) {
        map.forEach {
visit(TODO())
}
    }
}
