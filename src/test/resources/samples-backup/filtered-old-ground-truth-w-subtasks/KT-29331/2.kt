// Original bug: KT-29331
// Duplicated bug: KT-29331

interface WithIntId<T> {
    val T.intId get() = 1
}

object BooleanWithIntId : WithIntId<Boolean>

val x = BooleanWithIntId.run {
    true.intId
}
