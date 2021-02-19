// Original bug: KT-22098
// Duplicated bug: KT-22098

fun main(args: Array<String>) {
    Foo().run {
        object : Abstract(arrayOf("").map {
            buildString {
                append(getString(it))
            }
        }) {
        }
    }
}

fun Foo.getString(it: String) = ""

abstract class Abstract(val list: List<String>)
class Foo
