package com.stepanov.bbf.bugfinder.isolation.old

//import com.stepanov.bbf.coverage.data.Coverage
//import kotlinx.serialization.Serializable
//
//@Serializable
//class MutantCoverages() {
//
//    lateinit var original: Coverage
//
//    lateinit var failureCoverages: MutableList<Coverage>
//
//    lateinit var successCoverages: MutableList<Coverage>
//
//    private fun keepFirst(howMany: Int, where: MutableList<Coverage>) {
//        val iter = where.iterator()
//        var kept = 0
//        while (iter.hasNext()) {
//            iter.next()
//            if (kept >= howMany) iter.remove()
//            kept++
//        }
//    }
//
//    fun keepFirstFailures(howMany: Int) {
//        keepFirst(howMany, failureCoverages)
//    }
//
//    fun keepFirstSuccesses(howMany: Int) {
//        keepFirst(howMany, successCoverages)
//    }
//
//    private fun keepLast(howMany: Int, where: MutableList<Coverage>) {
//        val iter = where.iterator()
//        val howManyToRemove = where.size - howMany
//        var kept = 0
//        while (iter.hasNext()) {
//            iter.next()
//            if (kept < howMany) iter.remove()
//            kept++
//        }
//    }
//
//    fun keepLastFailures(howMany: Int) {
//        keepLast(howMany, failureCoverages)
//    }
//
//    fun keepLastSuccesses(howMany: Int) {
//        keepLast(howMany, successCoverages)
//    }
//
//}