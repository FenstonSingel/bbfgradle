
interface WithIntId<T> {
    val T.intId get() = 1
}
object BooleanWithIntId : WithIntId<Boolean>
val x = BooleanWithIntId.run {
    true.intId
}
