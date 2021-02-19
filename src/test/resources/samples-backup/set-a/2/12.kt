// Bug happens on JVM -Xnew-inference
//File: tmp/tmp0.kt
class MyList<T> {
operator fun get(index: Int?): T = TODO()
operator fun set( index: Int,value: T):Unit = TODO()
}
fun box()  {
    val list = MyList<Int>()
list[1]++
}