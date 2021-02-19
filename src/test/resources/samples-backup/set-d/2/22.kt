class DelegateInt {
operator fun getValue( thisRef: Any?,prop: Any
) = 1
operator fun setValue( thisRef: Any?,prop: Any,newValue: Int
):Unit = TODO()
}
fun testDelegateInt() {
    var localD by DelegateInt()
localD++
}