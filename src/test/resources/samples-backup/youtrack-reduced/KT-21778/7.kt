
val innerObject = object {
inline fun bar(action: () -> Unit):Unit = TODO()
fun foo() {
            bar {}
        }
    }
