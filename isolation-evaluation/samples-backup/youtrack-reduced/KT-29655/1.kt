
interface MyInterface<T> {
    val value: T
}
inline class MyInlineClass(val value: Int)
data class MyInlineClassInterface(override val value: MyInlineClass) : MyInterface<MyInlineClass>
