// Original bug: KT-35725
// Duplicated bug: KT-35725

class Foo {
    companion object {
        val field: String = super.toString()
    }
}
