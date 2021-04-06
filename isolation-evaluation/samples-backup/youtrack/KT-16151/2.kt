// Original bug: KT-16151
// Duplicated bug: KT-16151

fun main(args:Array<String>) {
    val relation: MutableMap<Long, String> = mutableMapOf()
    relation[1L] += "anyvalue"
}
