
fun main() {
    Foo().run2 {
        object : Abstract({
                    run {""}
                }()
                ) {}
    }
}
abstract class Abstract(
 list: String)
class Foo
fun <T, R> T.run2(block: T.() -> R): R = TODO()
