
open class OpenClass {
    open inner class OpenChild
}
object TestObject: OpenClass(){
        object TestChildObject: OpenChild()
    }
