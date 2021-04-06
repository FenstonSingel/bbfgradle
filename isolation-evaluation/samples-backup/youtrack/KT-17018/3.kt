// Original bug: KT-28579
// Duplicated bug: KT-17018

fun main() {
    var res = Test()

    scope { // This is crucial. If the expression is part of the main method, the code is compiled as expected.
        val func: (Test) -> Test = when (FakeExhaustive.SINGLE) {
            FakeExhaustive.SINGLE -> res::add
        }
    }
}

fun scope(action: () -> Unit): Unit = TODO()

enum class FakeExhaustive { SINGLE }

class Test {
    fun add(other: Test): Test = TODO()
}
