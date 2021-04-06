
class Outer {
    open inner class Inner
fun test() {
        open class Local1 : Inner()
        class Local2 : Local1()
    }
}
