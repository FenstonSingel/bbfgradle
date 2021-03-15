// Original bug: KT-38666
// Duplicated bug: KT-28956

fun foo() {
    lit@
    listOf(1, 2, 3, 4, 5).forEach {
        if (it == 3) return@lit // local return to the caller of the lambda, i.e. the forEach loop
        print(it)
    }
    
    print(" done with explicit label")
}

fun main() {
    foo()
}
