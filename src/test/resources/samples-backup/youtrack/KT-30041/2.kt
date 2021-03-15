// Original bug: KT-30041
// Duplicated bug: KT-30041

fun foo(block: suspend () -> Unit) {}
fun test() {
    suspend fun sus() {
        foo {
            sus()
        }
    }
}
