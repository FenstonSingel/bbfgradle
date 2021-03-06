//File Level.java
import kotlin.Metadata;

public enum Level {
   O,
   K;
}


//File Main.kt
// IGNORE_BACKEND_FIR: JVM_IR
// CHECK_CASES_COUNT: function=doTheThing count=2
// CHECK_IF_COUNT: function=doTheThing count=2

private fun Any?.doTheThing(): String {
    when (this) {
        is String -> return this
        is Level -> {
            when (this) {
                Level.O -> return Level.O.name
                Level.K -> return Level.K.name
            }
        }

        else -> return "fail"
    }
}


fun box(): String {
    return "O".doTheThing() + Level.K.doTheThing()
}

