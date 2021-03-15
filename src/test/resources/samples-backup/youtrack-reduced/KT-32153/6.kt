
fun eval(f: suspend () -> Int): Int = TODO()
fun outer() {
    suspend fun inner(): Int = eval { inner() }
}
