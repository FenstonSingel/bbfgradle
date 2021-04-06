
class Wrapper<T> {
fun <R> invoke(other: R) where R : T = TODO()
}
fun 
() {
    val w = Wrapper<Nothing>
    w(1)
}
