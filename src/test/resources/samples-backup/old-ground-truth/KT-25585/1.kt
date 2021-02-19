// Original bug: KT-25585
// Duplicated bug: KT-25585

import kotlin.coroutines.experimental.buildSequence

private fun id(n: Int) = n
private var f: (Int) -> Int = ::id

fun doIt() = buildSequence<Int> {
    f = if (true) ::id else ::id
}
/*
e: org.jetbrains.kotlin.util.KotlinFrontEndException: Exception while analyzing expression at ...
*/
