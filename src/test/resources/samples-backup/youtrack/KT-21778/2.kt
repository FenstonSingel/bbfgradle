// Original bug: KT-39563
// Duplicated bug: KT-21778

class C {
    val y = object : Runnable {

        inline fun String?.f() = this

        override fun run() {
            val x: String? = null
            x.f()
        }
    }
}
