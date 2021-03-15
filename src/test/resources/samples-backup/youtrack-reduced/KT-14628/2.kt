
open class ContentRoot {
    open inner class Directory : ContentRoot()
}
object Resources : ContentRoot() {
object b : Directory()
}
