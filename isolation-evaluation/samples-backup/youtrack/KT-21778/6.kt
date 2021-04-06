// Original bug: KT-13705
// Duplicated bug: KT-21778

val crash = object {
    fun main(args: Array<String>) {
        something()
    }

    inline fun something() = Unit
}
