// Bug happens on JVM -Xnew-inference
//File: tmp/tmp0.kt
operator fun IntArray.set( index: Long,elem: Int):Unit = TODO()
    var l = IntArray(TODO())
fun box()  {
operator fun IntArray.get(index: Long) = this
l[1.toLong()] += 1
}