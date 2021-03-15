// Original bug: KT-24805
// Duplicated bug: KT-24805

fun main() {
    test@
    test1@ for (i in 1..5) {
        break@test
    }
}
