// Original bug: KT-19861
// Duplicated bug: KT-19861

class Foo(var bar: Int)
fun main(){
    val baz = Foo(1)
    baz?.bar += 2
}
