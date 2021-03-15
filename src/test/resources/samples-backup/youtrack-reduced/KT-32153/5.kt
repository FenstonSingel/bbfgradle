
fun outer() {
  suspend fun inner(): Int = run { inner() }
}
