// Original bug: KT-33126
// Duplicated bug: KT-18344

typealias JagArray<E : JagArray<E>> = Array<E>
