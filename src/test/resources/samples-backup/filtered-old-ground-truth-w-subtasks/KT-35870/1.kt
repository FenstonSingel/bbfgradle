// Original bug: KT-27131
// Duplicated bug: KT-35870

enum class Enum(a: String) {
    ;
    init { println(a.length) }
    constructor()
}
