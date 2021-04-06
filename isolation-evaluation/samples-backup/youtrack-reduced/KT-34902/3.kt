
interface OneofField<T> {
    val value: T
    val number: Int
    val name: String
data class 
 constructor(
        override val value: UInt,
        override val number: Int = 1,
        override val name: String = ""
    ) : OneofField<UInt>
}
