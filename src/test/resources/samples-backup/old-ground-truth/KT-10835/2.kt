// Original bug: KT-21362
// Duplicated bug: KT-10835

open class A1(y: String) {
    val x = "[A1.x,$y]"
}

open class A2(y: String) {
    val x = "[A2.x,$y]"

    inner open class B1 : A1 {
        constructor(p: String) : super("[B1.param,$p]")

        fun foo() = x + ";" + this@A2.x + ";"
    }

    fun bar(): String {
        // Stacktrace1
        return with(A2("#bar")) {
            class C : B1("bar") {}
            C().foo()
        }
    }

    fun foo() = A2("#baz").baz()

    fun A2.baz(): String {
        // Stacktrace2
        class C : B1("baz") {}
        return C().foo()
    }
}

fun box(): String {
    val r3 = A2("f").bar()
    if (r3 != "[A1.x,[B1.param,bar]];[A2.x,#bar];") return "fail3: $r3"
    val r4 = A2("gg").foo()
    if (r4 != "[A1.x,[B1.param,baz]];[A2.x,#baz];") return "fail3: $r4"

    return "OK"
}

fun main(args: Array<String>) {
    println(box())
}
