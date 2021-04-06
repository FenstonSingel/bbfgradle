
abstract class Base {
    abstract inner class Inner
}
object Host : Base() {
    object Inner : Base.Inner()
}
