
fun main() {
  suspend fun innerFunction() {
    suspend {
      innerFunction()
    }
  }
}
