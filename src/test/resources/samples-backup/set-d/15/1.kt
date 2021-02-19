// Bug happens on JVM -Xnew-inference
//File: tmp/tmp0.kt

fun <T : Any
> assertEquals(box: T
 ) { assertEquals((Boolean::not)!! ) }