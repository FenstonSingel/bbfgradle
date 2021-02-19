// Parent bug: KT-36713

suspend inline fun f(g: suspend () -> Int): Int = g()

val condition = false

suspend fun main() {
    if (condition) {
        f { 1 }
    }
}
