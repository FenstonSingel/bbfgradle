// Bug happens on JVM -Xnew-inference
//File: tmp/tmp0.kt

var f:Any = TODO()
fun box()  {
    (::f)!!.set
}