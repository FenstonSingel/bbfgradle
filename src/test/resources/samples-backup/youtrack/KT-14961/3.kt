// Original bug: KT-34622
// Duplicated bug: KT-14961

class Luxer() {
    tailrec fun lux(l: String?): String {
        l?.let {
            return if(it.isBlank()) ""
            else lux(l.drop(1))
        }
        return ""
    }
}
