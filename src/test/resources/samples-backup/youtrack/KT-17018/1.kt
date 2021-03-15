// Original bug: KT-38704
// Duplicated bug: KT-17018

package calculator

import java.io.InputStreamReader
import kotlin.system.exitProcess

fun main() {
  InputStreamReader(System.`in`)
    .buffered()
    .forEachLine { line ->

      val continuation: () -> Unit =
        when (val input = parseInput(line)) {
          is EmptyLine -> ::noop
          is SingleInt -> output(input.value)
          is TwoInts -> output(input.first + input.second)
          is ExitCommand -> ::bye
          is Unknown -> output("Don't know what to do with input $input")
        }
      continuation()
    }
}

private fun bye() { println("Bye!"); exitProcess(0) }

private fun noop() {}

private fun output(value: Any): () -> Unit = { println(value) }

val WHITESPACE = "\\s".toRegex()

fun parseInput(line: String): CalcInput {
  val input = line.trim()
  if (input.isBlank())
    return EmptyLine
  else if (input == ExitCommand.commandText)
    return ExitCommand

  input.toIntOrNull() // try to parse the whole line as a single int
    ?.also {// parsing succeeded so return it
      return (SingleInt(it))
    }

  // Try to parse two ints, returning Unknown if there are more than 2 words
  input.split(WHITESPACE).let { words ->
    if (words.size > 2)
      return Unknown(line)
    val first = words.getOrNull(0)?.toInt()
    val second = words.getOrNull(1)?.toInt()
    if (first != null && second != null)
      return TwoInts(first, second)
  }

  // Failed to parse anything we understand so return Unknown
  return Unknown(line)
}

sealed class CalcInput
object EmptyLine : CalcInput()
data class SingleInt(val value: Int) : CalcInput()
data class TwoInts(val first: Int, val second: Int) : CalcInput()
object ExitCommand : CalcInput() {
  const val commandText = "/exit"
}
data class Unknown(val input: String) : CalcInput()
