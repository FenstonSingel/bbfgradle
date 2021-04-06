// Original bug: KT-8199
// Duplicated bug: KT-8199

open class A(x : () -> Unit)
class B(x : Int) : A({ class C(val y : Int = x) })
