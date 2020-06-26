package com.stepanov.bbf.bugfinder.isolation

//class CollapsedMutationStatistics() {
//
//    private val storage: MutableMap<String, Pair<MutableList<Double>, MutableList<Double>>> = mutableMapOf()
//
//    private var failuresCount = 0
//
//    private var successesCount = 0
//
//    private val Pair<MutableList<Double>, MutableList<Double>>.failures: MutableList<Double>
//        get() = this.first
//
//    private val Pair<MutableList<Double>, MutableList<Double>>.successes: MutableList<Double>
//        get() = this.second
//
//    operator fun plusAssign(collection: Collection<MutationStatistics>) {
//        for (element in collection) {
//            val storageElement = storage.getOrPut(element.name) { Pair(mutableListOf(), mutableListOf()) }
//            storageElement.failures += element.failures
//            storageElement.successes += element.successes
//            failuresCount += element.failures.size
//            successesCount += element.successes.size
//        }
//    }
//
//    fun removeIdenticalCoveragesStatistics() {
//        for ((_, lists) in storage) {
//            val failuresIter = lists.failures.iterator()
//            while (failuresIter.hasNext()) {
//                if (failuresIter.next() == 0.0) {
//                    failuresIter.remove()
//                    failuresCount--
//                }
//            }
//            val successesIter = lists.successes.iterator()
//            while (successesIter.hasNext()) {
//                if (successesIter.next() == 0.0) {
//                    successesIter.remove()
//                    successesCount--
//                }
//            }
//        }
//    }
//
//    val percentages: Map<String, Pair<Int, Int>>
//    get() {
//        val result = mutableMapOf<String, Pair<Int, Int>>()
//        for ((name, lists) in storage) {
//            val failuresPercentage = ((lists.failures.size.toDouble() / failuresCount) * 100).toInt()
//            val successesPercentage = ((lists.successes.size.toDouble() / successesCount) * 100).toInt()
//            result[name] = failuresPercentage to successesPercentage
//        }
//        return result
//    }
//
//}