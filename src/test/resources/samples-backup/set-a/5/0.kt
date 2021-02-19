// Bug happens on JVM -Xnew-inference
//File: tmp/tmp0.kt

class Nested
fun box() = (
::Nested)!!.result