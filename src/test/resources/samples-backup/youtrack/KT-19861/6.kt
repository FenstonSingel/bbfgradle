// Original bug: KT-31135
// Duplicated bug: KT-19861

class foo(var bar : String)

fun main() {    
    var baz : foo? = foo("test")
    baz?.bar += "test"
}
