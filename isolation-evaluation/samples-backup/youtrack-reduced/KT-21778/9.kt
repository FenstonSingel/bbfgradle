
fun main() {
    object {
        fun foo() = x.bar()
        val x = object {
            inline fun bar() = println()
        }
    }
}
