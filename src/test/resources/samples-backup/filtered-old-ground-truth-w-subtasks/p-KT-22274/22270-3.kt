// Original bug: KT-23698
// Duplicated bug: KT-22270

val isBefore: Boolean = init@ {
    if (true) return@init false
    true
}()
