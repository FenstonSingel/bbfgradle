// Bug happens on JVM -Xnew-inference
//File: tmp/tmp0.kt

class AShort(var value: Short) {
    operator fun get(i: Int?) = value
operator fun set( i: Int,newValue: Short):Unit = TODO()
}
fun box()  {
val aShort = AShort(1)
aShort[1]--
}