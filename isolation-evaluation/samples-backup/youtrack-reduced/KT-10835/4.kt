
fun <T, R> with2( receiver: T,block: T.() -> R): R = TODO()
class X {
    open inner class Y
fun foo() {
        with2(TODO(),{
            object : Y() {}
        })
    }
}
