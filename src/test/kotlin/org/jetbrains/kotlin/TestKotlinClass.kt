package org.jetbrains.kotlin

enum class TestingTableSwtich {
    ONE, TWO, THREE;
}

class TestKotlinClass {

    fun foo(int: Int) {
        if (int >= 0) {
            1 + 1
        }
        if (int >= 10000) {
            2 + 2
        }
        when (int) {
            42 -> 3 + 3
            720 -> 4 + 4
            else -> 5 + 5
        }
    }

    fun bar(int: Int?) {
        if (int == null) {
            2 + 2
        }
        val a = int!! + 5
        if (int === a) {
            1 + 1
        }
        when (TestingTableSwtich.values()[int % 3]) {
            TestingTableSwtich.ONE -> 3 + 3
            else -> 6 + 6
        }
    }

}