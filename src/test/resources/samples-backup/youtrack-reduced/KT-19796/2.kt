
import O.f
abstract class C(init: () -> Unit)
object O: C({ f() })  {
private fun f():Unit = TODO()
}
