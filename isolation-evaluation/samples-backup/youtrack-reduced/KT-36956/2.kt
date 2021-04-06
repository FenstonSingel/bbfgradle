
class A<T>(
 val value: T) {
    operator fun get(i: Int) = value
}
val aFloat = A(1.1f)
val aInt = (aFloat[1])--
