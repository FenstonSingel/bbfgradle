
fun bug() {
    foo{
        bar {
            prop
        }
    }
}
fun foo(a: A<Int>.() -> Unit):Unit = TODO()
class A<T> {
    val T.prop get() = ""
fun bar(f: Int.() -> Unit):Unit = TODO()
}
