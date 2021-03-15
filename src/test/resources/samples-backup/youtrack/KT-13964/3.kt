// Original bug: KT-26702
// Duplicated bug: KT-13964

package test

class C

operator fun <T : Number> C.invoke(a: T) = a.toString()

val String.c get() = C()

fun test() = "abc".c("def")
