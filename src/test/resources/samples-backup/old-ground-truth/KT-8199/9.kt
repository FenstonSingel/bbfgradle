// Original bug: KT-8199
// Duplicated bug: KT-8199

fun testMethod() {
  val f: () -> Int = {3}

  class FunClass(
      val x: Int = f()
  )
}
