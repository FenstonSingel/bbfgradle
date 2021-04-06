// Original bug: KT-25464
// Duplicated bug: KT-22498

class Bug {
  private fun createRunnable(nested: Nested): Runnable {
    return object : Runnable {
      override fun run() {
        bug()
      }

      private inline fun bug() {
        nested.toString()
      }
    }
  }

  private class Nested
}
