// IGNORE_BACKEND_FIR: JVM_IR
class A {
    operator fun component1() = 1
    operator fun component2() = 2
}


fun box() : String {
    val (a, b) = A()

    val local = object {
        public fun run() : Int {
            return a
        }
    }
    return if (local.run() == 1 && b == 2) "OK" else "fail"
}
