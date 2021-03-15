// Original bug: KT-34512
// Duplicated bug: KT-16789

package test

import kotlin.reflect.KFunction1

val samCtor: KFunction1<() -> Unit, Runnable> = ::Runnable
