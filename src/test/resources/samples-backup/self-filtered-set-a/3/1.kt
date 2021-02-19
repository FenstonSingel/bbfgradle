// Bug happens on JVM -Xnew-inference
//File: tmp/tmp0.kt

var backing = 1
class DelegateInt 
fun testDelegateInt() {
    var localD by 
TODO()
localD++
}
operator  fun DelegateInt.getValue( thisRef: Any?,prop: Any
) =
        backing