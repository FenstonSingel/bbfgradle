package org.jetbrains.kotlin.cli

import org.jetbrains.not.kotlin.TestingTableSwitch

class AnotherUselessTestKlass {

    fun testMethodCoverage1() {
        Unit
    }

    fun testMethodCoverage2() {
        Unit
    }

    fun testUnaryRefCmpCoverage(a: Any?) {
        if (a == null) Unit
        if (a != null) Unit
    }

    fun testBinaryRefCmpCoverage(a: Any) {
        val other = Any()
        if (a === other) Unit
        if (a !== other) Unit
    }

    fun testIntCmpCoverage(i: Int) {
        if (i >= 0) Unit
        if (i >= 5) Unit
    }

    fun testLookUpSwitchCoverage(i: Int) {
        when (i) {
            1 -> Unit
            9 -> Unit
            else -> Unit
        }
    }

    fun testTableSwitchCoverage(i: Int) {
        when (TestingTableSwitch.values()[i % 4]) {
            TestingTableSwitch.ONE -> Unit
            TestingTableSwitch.TWO -> Unit
            else -> Unit
        }
    }

}