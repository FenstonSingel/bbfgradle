//File Main.kt


fun foo(): Season = Season.SPRING
fun bar(): Season = Season.SPRING

fun box() : String {
    when (foo()) {
        bar() -> return "OK"
        else -> return "fail"
    }
}

// 0 TABLESWITCH



//File Season.java
import kotlin.Metadata;

public enum Season {
   WINTER,
   SPRING,
   SUMMER,
   AUTUMN;
}
