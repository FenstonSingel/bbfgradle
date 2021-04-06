
class A {
    fun test() {
        class MyActivityScope {
            init {
                this@A
            }
        }
class MyActivity  {            
            val z = MyActivityScope()
        }
    }
}
