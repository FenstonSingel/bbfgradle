// Original bug: KT-28496
// Duplicated bug: KT-14961

tailrec fun bar(num: Int?): String {
    num?.let{
        return bar( 1 )
    }
    return ""
}
