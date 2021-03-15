
import kotlin.properties.Delegates
fun main() {
    var k by Delegates.observable ( 1,TODO())
    k+=1
}
