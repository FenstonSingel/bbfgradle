// Original bug: KT-17018
// Duplicated bug: KT-17018

fun foo(block: () -> Unit) = block()

fun bar() { }
    
fun test() {
    foo {
        when (1) {
            1 -> bar()
            2 -> ::bar
        }
    }
}
