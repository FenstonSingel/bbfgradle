// Original bug: KT-38751
// Duplicated bug: KT-34902

interface SourceInstrumentId {
    val value: Long
}

inline class NodalPositionInstrumentId(override val value: Long) : SourceInstrumentId

interface Foo {
    val value: NodalPositionInstrumentId?
}

data class A(override val value: NodalPositionInstrumentId) : Foo
