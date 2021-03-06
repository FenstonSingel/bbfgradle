//File MyClass.java
import kotlin.Metadata;

@Ann(
   p1 = 2,
   p2 = 2,
   p3 = 2,
   p4 = 2L,
   p5 = 2.0D,
   p6 = 2.0F
)
public final class MyClass {
}


//File Main.kt
// IGNORE_BACKEND_FIR: JVM_IR
// TARGET_BACKEND: JVM

// WITH_RUNTIME

val prop1: Byte = 1 + 1
val prop2: Short = 1 + 1
val prop3: Int = 1 + 1
val prop4: Long = 1 + 1
val prop5: Double = 1.0 + 1.0
val prop6: Float = 1.0.toFloat() + 1.0.toFloat()

fun box(): String {
    val annotation = MyClass::class.java.getAnnotation(Ann::class.java)!!
    if (annotation.p1 != prop1) return "fail 1, expected = ${prop1}, actual = ${annotation.p1}"
    if (annotation.p2 != prop2) return "fail 2, expected = ${prop2}, actual = ${annotation.p2}"
    if (annotation.p3 != prop3) return "fail 3, expected = ${prop3}, actual = ${annotation.p3}"
    if (annotation.p4 != prop4) return "fail 4, expected = ${prop4}, actual = ${annotation.p4}"
    if (annotation.p5 != prop5) return "fail 5, expected = ${prop5}, actual = ${annotation.p5}"
    if (annotation.p6 != prop6) return "fail 6, expected = ${prop6}, actual = ${annotation.p6}"
    return "OK"
}



//File Ann.java
import java.lang.annotation.RetentionPolicy;
import kotlin.Metadata;
import kotlin.annotation.AnnotationRetention;
import kotlin.annotation.Retention;

@Retention(AnnotationRetention.RUNTIME)
@java.lang.annotation.Retention(RetentionPolicy.RUNTIME)
public @interface Ann {
   byte p1();

   short p2();

   int p3();

   long p4();

   double p5();

   float p6();
}
