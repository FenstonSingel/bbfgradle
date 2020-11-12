package com.stepanov.bbf.bugfinder.util

class BoundedSortedByModelElementSet<T>(
    private val modelEl: T,
    private val bound: Int,
    private val comparator: Comparator<T>,
    private val isSortingReversed: Boolean = false
) {

    /*
     * Comparator returns 0 if the objects are equal.
     * If the objects are not equal, the more different they are, the greater the result of the comparison.
     */

    val data = mutableListOf<Pair<T, Int>>()

    val size: Int
        get() = data.size

    fun add(a: T) {
        val c1 = comparator.compare(modelEl, a)
        for ((ind, el) in data.withIndex()) {
            if (a == el.first) break
            if ((c1 < el.second && !isSortingReversed) || (c1 > el.second && isSortingReversed)) {
                data.add(ind, a to c1)
                break
            }
            if (ind == data.size - 1) {
                data.add(a to c1)
                break
            }
        }
        if (data.isEmpty()) data.add(a to c1)
        if (data.size > bound) data.removeAt(data.size - 1)
    }

    fun first(): T = data.first().first

    fun toList(): List<T> =
        data.map { it.first }

    fun toMutableList(): MutableList<T> {
        val result = mutableListOf<T>()
        for (elem in data) {
            result += elem.first
        }
        return result
    }

    fun clear() {
        data.clear()
    }

}