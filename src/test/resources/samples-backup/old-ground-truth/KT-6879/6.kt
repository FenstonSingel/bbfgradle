// Original bug: KT-26697
// Duplicated bug: KT-6879

import java.util.*

class A() {
    fun foo(a: Int, b: Int): Boolean = a == b
    
    private fun foo() {
        open class B(val b: Int) {
            fun bar(other: B) = foo(other.b, b)
        }
        
        class C(b: Int, val c: Int) : B(b)
    }
}
