// Original bug: KT-36713
// Duplicated bug: KT-36713

suspend inline fun f(g: suspend () -> Int): Int = g() // [REDUNDANT_INLINE_SUSPEND_FUNCTION_TYPE] Redundant 'suspend' modifier: lambda parameters of suspend function type uses existing continuation.

val condition = false

suspend fun main() {
    if (condition) {
        f { 1 }
    }
}
