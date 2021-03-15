// Original bug: KT-36634
// Duplicated bug: KT-19796

import Object.fooBar

object Object : WrappedLambda(::fooBar) {
    private fun fooBar(): Unit = Unit
}

open class WrappedLambda(lambda: () -> Unit)