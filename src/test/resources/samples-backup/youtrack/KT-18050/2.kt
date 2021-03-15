// Original bug: KT-19127
// Duplicated bug: KT-18050

open class Glass <out T> (val t: T)
class SpecialGlass : Glass<*>()
