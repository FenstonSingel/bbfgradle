// Original bug: KT-40868
// Duplicated bug: KT-40868

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@ExperimentalContracts
private fun doTest(files: List<Boolean>) {
    for (file in files) {
        assertTrue { file == true }
    }
}

@ExperimentalContracts
fun assertTrue(block: () -> Boolean) {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
}
