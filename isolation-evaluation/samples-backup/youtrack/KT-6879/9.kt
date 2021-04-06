// Original bug: KT-6879
// Duplicated bug: KT-6879

class ABC {
    val outerProp: String = "OK"
    fun foo(): String {
        open class B {
            val prop = outerProp
        }
        class A : B()
        return A().prop
    }
}
