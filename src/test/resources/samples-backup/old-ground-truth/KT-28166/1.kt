// Original bug: KT-31460
// Duplicated bug: KT-28166

inline class A(val x : Int) {
}

fun <T: A> foo(a: T) {
  println(a.x)
}

fun main(args: Array<String>) {
  foo(A(32))
}
