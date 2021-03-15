// Original bug: KT-30720
// Duplicated bug: KT-29181

fun foo() {
  hh@  listOf(1, 2, 3, 4, 5).forEach h@{
        if (it == 3) return@hh // non-local return directly to the caller of foo()
        print(it)
    }
    println("this point is unreachable")
}
