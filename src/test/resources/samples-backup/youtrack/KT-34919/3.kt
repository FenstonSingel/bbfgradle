// Original bug: KT-34919
// Duplicated bug: KT-34919

typealias TA<X, Y> = (x: X) -> Y
abstract class Base<X, Y> : TA<X, Y>
class Impl : Base<Any, Any>()
