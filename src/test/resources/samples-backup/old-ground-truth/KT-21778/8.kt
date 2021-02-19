// Original bug: KT-21778
// Duplicated bug: KT-21778

val crash = object {
    fun main(args: Array<String>) {
        something()
    }

    inline fun something() = Unit
}
