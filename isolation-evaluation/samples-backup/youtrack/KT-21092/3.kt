// Original bug: KT-21092
// Duplicated bug: KT-21092

package test

class A<T>(val b: T)

fun main() {
    val a = {
        val prop = A(1).b::javaClass
        println(prop.name)
    }
    a()
}
