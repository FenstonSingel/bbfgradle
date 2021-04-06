
open class A(x : () -> Unit)
class B(x : Int) : A({ class C(
 y : Int = x) })
