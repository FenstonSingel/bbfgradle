package org.jetbrains.kotlin

class TestKotlinClass {

    fun foo(int: Int) {
        if (int >= 0) {
            1 + 1
        }
        if (int >= 10000) {
            2 + 2
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
    }

}