// !LANGUAGE: +StrictJavaNullabilityAssertions
// TARGET_BACKEND: JVM
// IGNORE_BACKEND_FIR: JVM_IR
// WITH_RUNTIME

// FILE: box.kt
import kotlin.test.*

fun box(): String {
    val actualValues = mutableListOf<Int>()
    for (i in J.arrayOfMaybeNullable()) {
        actualValues += i
    }
    assertEquals(listOf(42, null), actualValues)
    return "OK"
}

// FILE: J.java
public class J {
    public static Integer[] arrayOfMaybeNullable() {
        return new Integer[] { 42, null };
    }
}
