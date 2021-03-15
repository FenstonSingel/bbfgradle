// Original bug: KT-19796
// Duplicated bug: KT-19796

import O.f

abstract class C(init: () -> Unit)
object O: C({ f() })  { // or just ::f
    private fun f() {}
}
