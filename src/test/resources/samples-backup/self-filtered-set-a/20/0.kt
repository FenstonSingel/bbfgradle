// Bug happens on JVM -Xnew-inference
//File: tmp/tmp0.kt

typealias L
 = List
?
fun 
()  {
    val test: Int
test !is L
}