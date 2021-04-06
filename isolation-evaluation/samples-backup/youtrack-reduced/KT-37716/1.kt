
fun main() {
    bug<Any>()
}
inline fun <reified T> bug() {
{
        val prop = Unit
        object : Bar<T>({ prop }) {}
    }
}
abstract class Bar<T>(f: () -> Unit)
