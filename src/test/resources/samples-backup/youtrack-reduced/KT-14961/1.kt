
sealed class Rope {
    companion object {
        tailrec fun leafContaining(rope: Rope, i: Int): Pair<Leaf, Int> = when (rope) {
            is InnerNode -> {
                InnerNode().iter { child ->
                    return leafContaining( child,i)
}
throw IllegalStateException()
            }
            is Leaf -> Pair( TODO(),TODO())
        }
    }
}
class InnerNode
 :
    Rope() {
inline fun iter(f: (child: Rope) -> Boolean):Unit = TODO()
}
class Leaf
 : Rope() 
