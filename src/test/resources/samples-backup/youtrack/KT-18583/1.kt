// Original bug: KT-44657
// Duplicated bug: KT-18583

sealed class Foo<R> {

    fun getUrl() = when (this) {
        Bar -> "http://www.bar.com"
    }

    object Bar : Foo<Int>()
}

fun main() {
    println(Foo.Bar)
}
