
abstract class AbstractModule {
    abstract fun configure()
fun bind(c: Class<*>):Unit = TODO()
}
val module = object : AbstractModule() {
        override fun configure() {
            bind<Any>()
        }
inline fun <
 T> bind() = bind(TODO())
    }
