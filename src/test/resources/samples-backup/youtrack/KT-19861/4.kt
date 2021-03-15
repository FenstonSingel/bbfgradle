// Original bug: KT-37810
// Duplicated bug: KT-19861

package broken
class broken {
    var a = arrayListOf<b>()
    fun c() {
        a.lastOrNull()?.d += ""
    }
    data class b(var d: String) 
}
