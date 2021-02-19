// Bug happens on JVM -Xnew-inference
//File: tmp/tmp0.kt

open class A

class B : A()

fun box() {
    val b = B()
    (b::f)!!.get
}

var A.f
	get() = "" 
	set(value) = TODO()