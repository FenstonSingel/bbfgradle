
fun foo(block: suspend () -> Unit):Unit = TODO()
fun test() {
    suspend fun sus() {
        foo {
            sus()
        }
    }
}
