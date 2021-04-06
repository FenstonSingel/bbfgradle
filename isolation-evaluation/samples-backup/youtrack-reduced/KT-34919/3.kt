
typealias TA<X, Y> = (x: X) -> Y
class Base<X, Y> : TA<X, Y>
class Impl : Base<Any, Any>
