// Original bug: KT-34949
// Duplicated bug: KT-21778

abstract class AbstractModule {
    abstract fun configure()
    
    fun bind(c: Class<*>) {}
}

object Modules {
    private val module = object : AbstractModule() {
        override fun configure() {
            bind<Any>()
        }
        
        private inline fun <reified T> bind() = bind(T::class.java)
    }
}
