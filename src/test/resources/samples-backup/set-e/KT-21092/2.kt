// Parent bug: KT-21092

fun main() {
    A::foo      // OK
    String::foo // OK
    Int::foo    // crash
}

class A

val <T> T.foo get() = 42
