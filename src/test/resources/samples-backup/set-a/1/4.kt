// Bug happens on JVM -Xnew-inference
//File: tmp/tmp0.kt
fun box()  {
    fun foo(s: String?) = s
     (::foo)!!()
}