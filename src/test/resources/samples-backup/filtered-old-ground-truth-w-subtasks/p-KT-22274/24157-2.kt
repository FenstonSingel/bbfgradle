// Original bug: KT-24157
// Duplicated bug: KT-24157

fun main(args:Array<String>) {
    val BUG=label@Runnable{
        return@label
    }
}
