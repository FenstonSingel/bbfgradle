// Original bug: KT-28109
// Duplicated bug: KT-28109

fun main(args: Array<String>){
    var listOf = mutableListOf(1, 2, 3)
    (listOf[0])++
}
