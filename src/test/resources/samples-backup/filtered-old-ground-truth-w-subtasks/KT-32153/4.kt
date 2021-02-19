// Original bug: KT-32153
// Duplicated bug: KT-32153

/**
 * This example is minified.
 *
 * Moving `inner` outside `outer` or removing `suspend` modifier should make compilation to succeed.
 * Removing `run` also solves the issue.
 */
fun outer() {
  suspend fun inner(): Int = run { inner() }
}
