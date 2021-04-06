// Original bug: KT-4454
// Duplicated bug: KT-4462

class Bar {
    fun invoke(x: Int): Int = x
}
class Foo {
    val get: Bar = Bar()
}
 
fun foo () {
    Foo().get(1)
    Foo()[1]
}
