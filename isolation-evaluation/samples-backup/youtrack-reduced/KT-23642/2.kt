
inline fun fun1(crossinline lambda1: () -> Unit) {
    fun2{ lambda1() }
}
inline fun fun2(crossinline lambda2: () -> Unit) {
    object {
        fun method1() {
            fun3(lambda2)
        }
    }
}
inline fun fun3(crossinline lambda3: () -> Unit) {
    object {
        fun method1() {
            lambda3()
        }
    }
}
