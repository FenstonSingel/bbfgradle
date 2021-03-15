// Original bug: KT-34543
// Duplicated bug: KT-19861

class Foo(var bar: String)

fun main(){
	var baz: Foo? = Foo("test")
	baz?.bar += "test"
	println(baz)
}
