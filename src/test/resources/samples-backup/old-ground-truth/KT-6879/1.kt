// Original bug: KT-33379
// Duplicated bug: KT-6879

class C{
    open inner class A
    fun f(): A{
        open class B : A()
    	val v = object : B() {}
        return v
    }
}
