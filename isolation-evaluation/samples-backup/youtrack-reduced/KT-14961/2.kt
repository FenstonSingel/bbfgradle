
tailrec fun leafContaining(rope: Rope, i: Int): Pair<Rope, Int> = when (rope) {
    is InnerNode -> {
        InnerNode().iter { child ->
            return leafContaining( child,i)
}
throw IllegalStateException()
    }
}
sealed class Rope
class InnerNode : Rope() {
    inline fun iter(f: (child: Rope) -> Boolean):Unit = TODO()
}
