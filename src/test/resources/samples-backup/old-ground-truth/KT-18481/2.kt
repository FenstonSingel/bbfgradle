// Original bug: KT-18481
// Duplicated bug: KT-18481

object A {
    const val a = B.b
}

object B {
    const val b = A.a
}
