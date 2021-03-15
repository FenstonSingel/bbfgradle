// Original bug: KT-41416
// Duplicated bug: KT-4113

class Test {

    val nullableCheckBox: B? = null

    fun fail(): DialogPanel {
        return panel {
            if (nullableCheckBox != null) {
                row { nullableCheckBox() }
            }
        }
    }

}

class DialogPanel

inline fun panel(init: B.() -> Unit): DialogPanel {
    return DialogPanel()
}

open class A

class B : A() {
    fun row(test: () -> Unit) {

    }
}

operator fun <T : A> T.invoke(
): Unit = TODO()
