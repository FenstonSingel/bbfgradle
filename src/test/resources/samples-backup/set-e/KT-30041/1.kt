// Parent bug: KT-30041

fun main() {
  suspend fun innerFunction() {
    suspend {
      innerFunction()
    }
  }
}
