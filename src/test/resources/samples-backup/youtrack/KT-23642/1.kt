// Original bug: KT-23642
// Duplicated bug: KT-23642

package com.empowerops.language

class SequentialPoolBroker2 {

    inline fun <T> doSubmit(crossinline task: () -> T): Unit {

        val newJob = { task.invoke() }

    }

    fun another(command: Runnable): Unit = doSchedule { command.run() }

    inline fun <V : Any?> doSchedule(crossinline callable: () -> V): Unit {

        actualSchedule { doSubmit(callable) }

    }

    fun <T> actualSchedule(command: () -> T): Unit {}

}
