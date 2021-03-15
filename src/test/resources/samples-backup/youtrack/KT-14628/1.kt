// Original bug: KT-43671
// Duplicated bug: KT-14628

open class OpenClass {
    open inner class OpenChild
}

class TestClass{
    object TestObject: OpenClass(){
        object TestChildObject: OpenChild()
    }
}
