// Original bug: KT-28319
// Duplicated bug: KT-28319

class A : B<*>(42) // [PROJECTION_IN_IMMEDIATE_ARGUMENT_TO_SUPERTYPE] Projections are not allowed for immediate arguments of a supertype
open class B<T>(val t: T)

