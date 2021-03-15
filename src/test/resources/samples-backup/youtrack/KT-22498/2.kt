// Original bug: KT-25464
// Duplicated bug: KT-22498

class Foo {
    fun bar(param: Any): Any {
        return object {
            fun run() {
                bug()
            }

            inline fun bug() {
                param
            }
        }
    }
}
