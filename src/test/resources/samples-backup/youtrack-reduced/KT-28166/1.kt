
inline class A(val x : Int)
fun <T: A> foo(a: T):Unit = TODO()
fun main() {
  foo(A(1))
}
