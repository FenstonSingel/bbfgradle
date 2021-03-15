
fun listOfFactor(): List<Int> {
  tailrec fun listOfFactor(number: Int, acc: List<Int>): List<Int> {
    (1..number).forEach {
      return listOfFactor( number,acc + it)
    }
    return acc
  }
  return listOf()
}
