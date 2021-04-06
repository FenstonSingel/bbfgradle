
suspend inline fun f(g: suspend () -> Int)  = 
1
val condition = false
suspend fun main() {
    if (condition) 
        f { 1 }
}
