
import kotlin.reflect.KFunction1
val samCtor: KFunction1<() -> Unit, Runnable> = ::Runnable
