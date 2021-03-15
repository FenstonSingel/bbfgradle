
import kotlin.properties.*
fun main() {
    var blah: Int by Delegates.blah()
blah++
}
fun <T> Delegates.blah(): ReadWriteProperty<Any?, T> = TODO()
