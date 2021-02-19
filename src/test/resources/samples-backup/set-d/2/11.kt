// Bug happens on JVM -Xnew-inference
//File: tmp/tmp0.kt
operator fun IntArray.set( index: Long,elem: Int):Unit = TODO()
fun box()  {
operator fun IntArray.get(index: Long) = this
var l = IntArray(1)
l[1.toLong()] += 1
}