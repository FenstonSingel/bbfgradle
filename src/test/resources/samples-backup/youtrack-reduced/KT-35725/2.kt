
abstract class Base {
    open val field: Int = TODO()
}
class Message {
    companion object: Base() {
        override val field: Int = super.field
    }
}
