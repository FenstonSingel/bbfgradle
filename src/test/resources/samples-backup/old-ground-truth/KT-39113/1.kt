// Original bug: KT-39113
// Duplicated bug: KT-39113

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@ExperimentalContracts
fun main() {
    mapOf("" to "").forEach { (a, b) ->
        runBlocking {
            a
        }
    }
}

@ExperimentalContracts
/*inline*/ fun runBlocking(block: () -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    block()
}
