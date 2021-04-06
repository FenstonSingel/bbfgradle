// Original bug: KT-34407
// Duplicated bug: KT-25694

inline fun some(
   param1:()->String = {"$param2 vasya"},
   param2:String="Hello,"
) = println(param1())

fun main(){
    some()
}
