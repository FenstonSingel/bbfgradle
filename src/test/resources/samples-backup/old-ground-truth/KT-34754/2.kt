// Original bug: KT-34754
// Duplicated bug: KT-34754

data class Tree(val value: Int, val children: List<Tree>)

fun Tree.asLinearized(): Sequence<Int> = sequence {
    suspend fun SequenceScope<Int>.linearize(tree: Tree) {
        yield(tree.value)
        tree.children.forEach { linearize(it) }
    }
    linearize(this@asLinearized)
}
