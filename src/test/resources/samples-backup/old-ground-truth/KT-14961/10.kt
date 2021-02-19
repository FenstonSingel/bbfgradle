// Original bug: KT-14961
// Duplicated bug: KT-14961

tailrec fun leafContaining(rope: Rope, i: Int): Pair<Rope, Int> = when (rope) {
    is InnerNode -> {
        rope.iter { child ->
            if (i < 1) return leafContaining(child, i)
            false
        }

        throw IllegalStateException()
    }
}
sealed class Rope

private data class InnerNode(val size: Int) : Rope() {
    inline fun iter(f: (child: Rope) -> Boolean) {
    }
}
