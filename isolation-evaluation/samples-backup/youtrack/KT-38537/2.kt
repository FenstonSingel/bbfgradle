// Original bug: KT-38621
// Duplicated bug: KT-38537

fun main() {
    val rawCrawl = """ A long time ago,
    in a galaxy 
    far far away ...
    BUMM BUMM BUMM""".trimMargin(" ")
    println(rawCrawl)
}
