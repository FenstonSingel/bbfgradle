// Original bug: KT-36956
// Duplicated bug: KT-36956

class A<T>(private val value: T) {
    operator fun get(i: Int) = value
}

val aFloat = A<Float>(TODO())

val aInt = (aFloat[1])--
