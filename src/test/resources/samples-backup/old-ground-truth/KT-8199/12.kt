// Original bug: KT-8199
// Duplicated bug: KT-8199

fun main() {
    
    val aVal = A()
    
    data class B(
       val a: A = aVal
    )
    
    print(B())
    
}

class A
