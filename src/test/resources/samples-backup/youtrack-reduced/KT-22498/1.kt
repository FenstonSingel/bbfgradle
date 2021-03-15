
fun createRunnable(nested: Nested)  {
     object : Runnable {
      override fun run() {
        bug()
      }
inline fun bug() {
        nested.toString()
      }
    }
  }
class Nested
