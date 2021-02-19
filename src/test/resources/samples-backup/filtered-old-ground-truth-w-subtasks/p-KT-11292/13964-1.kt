// Original bug: KT-31496
// Duplicated bug: KT-13964

object Obj {
  operator fun <A : Number> invoke(value: A) {}
}

fun example() = Obj("not a number")
