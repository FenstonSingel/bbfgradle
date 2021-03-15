
class Tree(
 val children: List<Tree>)
fun Tree.asLinearized(): Sequence<Int> = sequence {
    suspend fun SequenceScope<Int>.linearize(tree: Tree) {
tree.children.forEach { linearize(TODO()) }
    }
}
