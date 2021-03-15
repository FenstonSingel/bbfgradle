
import Object.fooBar
object Object : WrappedLambda(::fooBar) {
    private fun fooBar(): Unit = TODO()
}
open class WrappedLambda(lambda: () -> Unit)