// Original bug: KT-41881
// Duplicated bug: KT-32153

class Reproducer() {

    suspend fun f(a: List<String>) {
        suspend fun recurse(current: List<String>) {
            current.forEach { recurse(listOf(it)) }
        }

        recurse(a)
    }
}
