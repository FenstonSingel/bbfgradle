// Original bug: KT-32816
// Duplicated bug: KT-21778

public class Goo {
    private val boo = object : Any() {
        public inline fun <reified T> foo(): T? {
            return null
        }
    }
    
    fun goo():String? {return boo.foo()}
}

fun main(args: Array<String>) {
    var x : String? = Goo().goo()
    println(x)
}
