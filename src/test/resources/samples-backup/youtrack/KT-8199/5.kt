// Original bug: KT-23308
// Duplicated bug: KT-8199

fun main(args: Array<String>) {
    val i = 0

    class D(val j: Int = i)

    print(D())
}
