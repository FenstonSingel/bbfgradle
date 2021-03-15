// Original bug: KT-22959
// Duplicated bug: KT-22270

data class Thing(val start: Long, val end: Long)

fun matchEvents2(dbEvents: MutableList<Thing>, newEvents: MutableList<Thing>): List<Pair<Thing, Thing>> {
    val res = mutableListOf<Pair<Thing, Thing>>()

    val baseIter = newEvents.iterator()
    baseIter.forEach {base ->
        val eventIter = dbEvents.iterator()

        // if I expose the label the compiler crashes with an NPE
        plop@ eventIter.forEach { dbEvent ->
            if (base.start == dbEvent.start) {
                res.add(Pair(dbEvent, base))
                baseIter.remove()
                eventIter.remove()
                return@plop
            }
        }
    }
    return res
}
