
class A {
var d = D()
}
class D
operator fun D?.inc() = this
fun foo(a: A?) {
a?.d++
}
