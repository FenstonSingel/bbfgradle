// Original bug: KT-9874
// Duplicated bug: KT-8199

fun bar() {
    val x = 1
    class A(val y: Int = x)
    print(A().y)
}

fun main(args: Array<String>) {
    bar()
}
