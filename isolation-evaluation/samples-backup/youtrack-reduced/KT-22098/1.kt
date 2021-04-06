
fun String.test() {
        object : F({ run {TODO()} }) {}
    }
open class F(
f: () -> String
)
