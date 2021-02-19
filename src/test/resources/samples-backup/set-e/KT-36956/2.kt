// Parent bug: KT-36956

class A<T>(private val value: T) {
    operator fun get(i: Int) = value
    operator fun set(i: Int, v: T) {}
}

val aFloat = A(0.0f)

val aInt = (aFloat[1])--
