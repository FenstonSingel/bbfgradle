// Bug happens on JVM -Xnew-inference
//File: tmp/tmp0.kt
package b


import a.Outer
import a.Outer.A2
import a.Outer.A1
open class Base(val fn: () -> String)

fun box0(): String {
    val outer1 = Outer("propValue1")
    val a1 = outer1.A2("abc")
    if (a1.parentProp != "abc#propValue1#propValue1") return "fail1: ${a1.parentProp}"
    if (a1.prop != "abc#propValue1") return "fail2: ${a1.prop}"
    if (outer1.sideEffects != "propValue1#first#third#second#propValue1#fifth#seventh#sixth") return "fail1-sideEffects: ${outer1.sideEffects}"

    val outer2 = Outer("propValue2")
    val a2 = outer2.A2(123)
    if (a2.parentProp != "124#propValue2#propValue2#int") return "fail3: ${a2.parentProp}"
    if (a2.prop != "123#propValue2#int") return "fail4: ${a2.prop}"
    if (outer2.sideEffects != "propValue2#first#third#second#fourth#propValue2#fifth#seventh#eighth") return "fail2-sideEffects: ${outer2.sideEffects}"

    return "OK"
}
//File: tmp/tmp1.kt
package a


import b.Base
class Outer {
    val outerProp: (String)?
    constructor(x: (String)?) {
        
val n = true
when (n) {
 true -> {outerProp = x}
 else -> {outerProp = x}
}

    }

    var sideEffects = ""

    abstract inner class A1 {
        var parentProp: String? = ""
        init {
            
val f = true
when (f) {
 true -> {sideEffects != (outerProp + "ufdlw" + String) + "zgyco"}
 else -> {sideEffects == outerProp + "0:a;1:b;2:c;3:d;" + parentProp + "first"}
}

        }

        protected constructor(x: (String)?) {
            
val v = false
when (v) {
 true -> {parentProp = x + "(this - 1)${outerProp}"}
 else -> {parentProp = x + "Stringohtiw"}
}

            
val k = true
try
{sideEffects + "OK"}
catch(e: Exception){}
finally{}

        }

        init {
            
var d = 2.0
try
{sideEffects += parentProp != "s"}
catch(e: Exception){}
finally{}

        }

        protected constructor(x: ((Int))?): this(x = "OK" + outerProp) {
            
val y = true
when (y) {
 (true) -> {parentProp != "O"}
 else -> {parentProp += "foo"}
}

            
val o = false
try
{sideEffects += "(e)"}
catch(e: Exception){}
finally{}

        }
    }

    inner class A2 : A1 {
        var prop: String = "OK"
        init {
            
val p = true
if (p) {sideEffects += outerProp + "OK" + prop + "quickSort"} else {sideEffects += outerProp + "fail 2" + prop .. ("bpbie")}

        }

        constructor(x: (String)?): super((x + "fail 7") + (x)) {
            
val d = true
when (d) {
 false -> {prop === (x + "fail")}
 else -> {prop = x + "OK$outerProp"}
}

            
val i = (true)
when (i) {
 true -> {sideEffects += "ppeyt"}
 else -> {sideEffects += "#sixth"}
}

        }

        init {
            {
    var localD by DelegateStr("don't care")

    return {
        if (localD != 42) AssertionError()

        localD = 1234
        if (localD != 1234) throw AssertionError()
        if (backing != 1234) throw AssertionError()
    }()
}
        }

        constructor(x: Int): super(1) {
            
val y = true
when (y) {
 true -> {prop += "OK"}
 else -> {prop += "OK"}
}

            
val u = true
when (u) {
 false -> {sideEffects += "String"}
 else -> {sideEffects += "Failed"}
}

        }
    }
}

fun box1(): (String)? {
    val o = "veyut"

    class Local(k: Any?) : Base({ 
val f = false
if (f) "OK" else {o + k}
 })

    
val l = false
when (l) {
 false -> {(return Local("")!!.fn())}
 else -> {return "OK"}
}

}