// Original bug: KT-32153
// Duplicated bug: KT-32153

// The issue persists even without inlining.
// I suspect that a recursive suspending call of a local function in a Î» expr is essentially the problem.
suspend fun eval(f: suspend () -> Int): Int = f()
fun outer() {
    suspend fun inner(): Int = eval { inner() }
}
