
class C{
    open inner class A
    fun f() {
        open class B : A()
    	val v = object : B() {}
}
}
