// Original bug: KT-29331
// Duplicated bug: KT-29331

fun bug() {
    foo({
        bar {
            prop
        }
    })
}

fun foo(a: A<Int>.() -> Unit) {}

class A<T> {
    val T.prop get() = ""

    fun bar(f: Int.() -> Unit) {
        42.apply(f)
    }
}
