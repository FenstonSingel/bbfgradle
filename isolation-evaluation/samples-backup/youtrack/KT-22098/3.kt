// Original bug: KT-22098
// Duplicated bug: KT-22098

fun main(args: Array<String>) {
    Foo().run2 {
        object : Abstract(
                {
                    getString("")
                }()
        ) {
        }
    }
}

fun Foo.getString(it: String) = ""

abstract class Abstract(val list: String)
class Foo

fun <T, R> T.run2(block: T.() -> R): R {
    return block()
}

