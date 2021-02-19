// Original bug: KT-27512
// Duplicated bug: KT-18583

sealed class Option<out T> {
    class Some<T>(val value: T) : Option<T>()
    object None : Option<Nothing>()

    fun size() = when (this) {
        is Some -> 1
        None -> 0
    }
}
