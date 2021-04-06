// Original bug: KT-16789
// Duplicated bug: KT-16789

fun foo(action: () -> Unit) {
  val runnable = action.let(::Runnable)
  runnable.run()
}
