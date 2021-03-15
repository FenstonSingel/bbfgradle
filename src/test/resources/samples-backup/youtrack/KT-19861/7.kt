// Original bug: KT-21081
// Duplicated bug: KT-19861

// WITH_RUNTIME
class A(var value: Int)

operator fun A?.plus(a: A) = A(this?.value ?: 0 + a.value)

class B(var a: A)

fun box(): String {
    var b: B? = B(A(11))
    b?.a += A(31)
    if (b?.a?.value != 42) return "FAIL 0"
    return "OK"
}
