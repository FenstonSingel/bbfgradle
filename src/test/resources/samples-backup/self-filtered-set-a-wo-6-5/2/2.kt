// Bug happens on JVM -Xnew-inference
//File: tmp/tmp0.kt

operator fun Long.get(i: Int?) = this
    operator fun Long.set( i: Int,newValue: Long):Unit = TODO()
var x = 1L
        val y = x[1]++