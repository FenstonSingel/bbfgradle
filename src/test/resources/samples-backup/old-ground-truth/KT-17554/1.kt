// Original bug: KT-23681
// Duplicated bug: KT-17554

fun main(args: Array<String>) {
  var increaseMe: Long = 0
  val enum = Enum.ONE

  @Suppress("NON_EXHAUSTIVE_WHEN")
  when (enum) {
    Enum.ONE -> increaseMe++
    Enum.TWO -> increaseMe++
  }

  println(increaseMe)
}

enum class Enum {
  ONE, TWO, THREE
}
