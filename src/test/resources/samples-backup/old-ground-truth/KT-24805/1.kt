// Original bug: KT-39087
// Duplicated bug: KT-24805

fun main() {
    test@ 
    test1@ for (i in 1..5) {
        println("test1 $i")
        if(i==3) break@test
    }
    test2@ for(i in 1..5){
        println("test2 $i")
    }
    println("finished")
}
