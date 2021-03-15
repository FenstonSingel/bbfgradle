
class A<T>(
 val value: T) {
    operator fun get(i: Int) = value
}
val aFloat = A<Float>(TODO())
val aInt = (aFloat[1])--
