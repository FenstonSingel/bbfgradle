package org.jetbrains.kotlin

enum class TestingTableSwitch {
    ONE, TWO, THREE, FOUR;
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
        when (TestingTableSwitch.values()[int % 4]) {
            TestingTableSwitch.ONE -> 3 + 3
            TestingTableSwitch.TWO -> 4 + 4
            else -> 6 + 6
        }
    }

}