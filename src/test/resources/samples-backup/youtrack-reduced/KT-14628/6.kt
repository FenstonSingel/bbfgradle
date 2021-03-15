
abstract class BaseClass<TItem, TBuilder> {
    abstract inner class Builder {
        abstract fun append(item: TItem)
    }
}
object ImplForByte: BaseClass<Byte, ImplForByte.ByteBuilder>() {
    class ByteBuilder: BaseClass<Byte, ImplForByte.ByteBuilder>.Builder() {
        override fun append(item: Byte)  =
            TODO()
    }
}
