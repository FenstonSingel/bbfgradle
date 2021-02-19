// Original bug: KT-37845
// Duplicated bug: KT-14961

package me.viluon.ion

sealed class Rope {
    companion object {
        tailrec fun leafContaining(rope: Rope, i: Int): Pair<Leaf, Int> = when (rope) {
            is InnerNode -> {
                rope.iter { child ->
                    if (i < 1) return leafContaining(child, i)
                    false
                }

                throw IllegalStateException()
            }
            is Leaf -> Pair(rope, i)
        }
    }


    fun leafContaining(i: Int): Pair<Leaf, Int> = Rope.leafContaining(this, i)
}

private data class InnerNode(val size: Int, val children: Array<Rope?> = Array(2) { null }) :
    Rope() {
    internal inline fun iter(f: (child: Rope) -> Boolean) {
        for (i in 0 until size.toInt()) if (f(children[i]!!)) break
    }
}

data class Leaf(val data: String) : Rope() {
    val size: UInt
        inline get() = data.length.toUInt()
}
