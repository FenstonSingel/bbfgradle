// Original bug: KT-14961
// Duplicated bug: KT-14961

fun listOfFactor(number: Int): List<Int> {
  tailrec fun listOfFactor(number: Int, acc: List<Int>): List<Int> {
    (2..number).forEach {
      if (number % it == 0) return listOfFactor(number / it, acc + it)
    }
    return acc
  }
  return listOfFactor(number, emptyList())
}

fun main(args: Array<String>) {
  println(listOfFactor(60))
}
