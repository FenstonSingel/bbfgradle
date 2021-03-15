// Original bug: KT-32734
// Duplicated bug: KT-30041

fun main() {
  suspend fun innerFunction() {
    suspend {
      innerFunction()
    }
  }
}
