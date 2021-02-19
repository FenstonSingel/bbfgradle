// Original bug: KT-36217
// Duplicated bug: KT-22274

fun (() -> Unit).ext(): String = "OK"

fun box() =
    foo@{
        return@foo
    }.ext()

fun main() {
    println(box())
}
