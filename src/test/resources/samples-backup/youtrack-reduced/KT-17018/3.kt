
fun 
() {
    var res = Test()
scope {
val func: () -> Test = when (FakeExhaustive.SINGLE) {
            FakeExhaustive.SINGLE -> res::add
        }
    }
}
class Test {
    fun add()  = TODO()
}
