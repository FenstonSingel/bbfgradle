// Original bug: KT-41034
// Duplicated bug: KT-41034

class A {
    var b = B()
    var c = C()
    var d = D()
}

class B {
    operator fun inc(): B = this
}

class C
operator fun C.inc() = this

class D
operator fun D?.inc() = this

fun foo(a: A?) {
    // Error: UNSAFE_OPERATOR_CALL
    //a?.b++
    // Error: UNSAFE_OPERATOR_CALL
    //a?.c++

    // Resolved sucessfully, backend-internal error
    a?.d++
}
