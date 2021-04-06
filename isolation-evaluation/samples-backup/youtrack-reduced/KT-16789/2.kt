
fun foo(action: () -> Unit) {
  val runnable = action.let(::Runnable)
}
