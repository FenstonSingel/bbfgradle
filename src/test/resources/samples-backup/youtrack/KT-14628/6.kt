// Original bug: KT-14628
// Duplicated bug: KT-14628

abstract class BaseClass<TItem, TBuilder> {
    abstract inner class Builder {
        abstract fun append(item: TItem)
    }
}

// if I change to class here, all compiles well
object ImplForByte: BaseClass<Byte, ImplForByte.ByteBuilder>() {
    class ByteBuilder: BaseClass<Byte, ImplForByte.ByteBuilder>.Builder() {
        override fun append(item: Byte): Unit =
            TODO("not implemented")
    }
}
