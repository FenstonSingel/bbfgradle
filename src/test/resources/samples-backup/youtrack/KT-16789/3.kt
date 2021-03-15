// Original bug: KT-16789
// Duplicated bug: KT-16789

import kotlin.reflect.KFunction1
val samCtor: KFunction1<() -> Unit, Runnable> = ::Runnable
