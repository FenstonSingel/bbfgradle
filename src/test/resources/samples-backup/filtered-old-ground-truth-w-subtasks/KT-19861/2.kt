// Original bug: KT-38657
// Duplicated bug: KT-19861

class Top{
    fun whatever() {
        //builds just fine:
        //Mid().i?.attribute = Mid().i?.attribute + "a"
        //breaks the compiler:
        Mid().i?.attribute += "a"
    }
}
class Mid(var i: Bottom?=null) {

}
class Bottom{
    var attribute: String = ""
}
