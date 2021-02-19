// Bug happens on JVM -Xnew-inference
//File: tmp/tmp0.kt
fun box()  {
    class A {}
(::A)!!
.result
}