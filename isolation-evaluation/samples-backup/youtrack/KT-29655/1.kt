// Original bug: KT-30567
// Duplicated bug: KT-29655

interface MyInterface<T> {
    val value: T
}

inline class MyInlineClass(val value: Int)

data class MyInlineClassInterface(override val value: MyInlineClass) : MyInterface<MyInlineClass>
