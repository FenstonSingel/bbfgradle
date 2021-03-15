// Original bug: KT-14961
// Duplicated bug: KT-14961

private tailrec fun bar(num: Int): String =
    with(num) {
        return bar(this)
    }
