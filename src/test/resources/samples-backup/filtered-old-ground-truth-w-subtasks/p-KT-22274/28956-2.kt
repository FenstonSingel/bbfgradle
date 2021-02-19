// Original bug: KT-28956
// Duplicated bug: KT-28956

fun test(monitor: Any) {
  synchronized(monitor) {
    loop@ for (i in 1..100) {
      return@loop
    }
  }
}
