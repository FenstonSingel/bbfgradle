// Original bug: KT-35067
// Duplicated bug: KT-30102

class DelegateWithPrimitiveWrapperEqualsBug(value: Double): Comparable<Double> by value {
    val value_ = value
}

fun main() {
    println("Hello, world!!!")
}
