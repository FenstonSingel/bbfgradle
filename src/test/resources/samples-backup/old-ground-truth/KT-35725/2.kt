// Original bug: KT-35725
// Duplicated bug: KT-35725

abstract class Base {
    open val field: Int = 0
}

class Message {
    companion object: Base() {
        override val field: Int = super.field
    }
}
