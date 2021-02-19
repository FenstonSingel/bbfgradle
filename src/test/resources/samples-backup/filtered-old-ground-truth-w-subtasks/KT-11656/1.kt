// Original bug: KT-21860
// Duplicated bug: KT-11656

interface Foo {
    fun name(): String
}

interface Bar: Foo {
    override fun name() = name
    var name: String
}

enum class Baz : Bar {
    ONE, TWO, TREE;
}
