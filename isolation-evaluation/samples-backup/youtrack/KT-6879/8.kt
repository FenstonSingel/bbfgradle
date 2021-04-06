// Original bug: KT-11000
// Duplicated bug: KT-6879

class A {
    fun test() {
        class MyActivityScope {
            init {
                this@A
            }
        }

        class MyActivity()  {            
            val z = MyActivityScope()
        }
    }
}

