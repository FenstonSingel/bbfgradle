// Original bug: KT-34902
// Duplicated bug: KT-34902

interface OneofField<T> {
    val value: T
    val number: Int
    val name: String

    @UseExperimental(ExperimentalUnsignedTypes::class)
    data class OneofUint32 constructor(
        override val value: UInt,
        override val number: Int = 111,
        override val name: String = "oneof_uint32"
    ) : OneofField<UInt>
}
