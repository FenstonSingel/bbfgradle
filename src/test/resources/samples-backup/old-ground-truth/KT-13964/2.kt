// Original bug: KT-30680
// Duplicated bug: KT-13964

interface MyInterface

open class MyBaseClass

class Hello {
  companion object {
    operator fun <T> invoke(t: T) where T : MyBaseClass, T : MyInterface {
    }
  }
}

fun oops() {
  Hello(MyBaseClass())
}
