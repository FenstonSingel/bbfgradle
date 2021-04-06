
class X {
    open inner class Y
fun foo() {
        with(TODO(),{
            object : Y() {}
        })
    }
}
