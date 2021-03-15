// Original bug: KT-21778
// Duplicated bug: KT-21778

class SomeClass {
    private val innerObject = object {
        private inline fun bar(action: () -> Unit) {
            action()
        }

        fun foo() {
            bar { println("foo") }
        }
    }
}
