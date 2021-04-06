// Original bug: KT-34213
// Duplicated bug: KT-32153

suspend fun anotherOuterSuspendFun(){
}
suspend fun outerSuspendFun() {
    suspend fun anotherInnerSuspendFun(){
    }
    suspend fun doInnerSuspend() {
        //case 1: use inline fun and recurse, CompilationException
        Unit.let {
            doInnerSuspend()
        }

        //case 2: no inline fun but recurse, works well
        doInnerSuspend()

        //case 3: use inline fun, but no recurse, works well
        Unit.let {
            println()
        }

        //case 4: use inline fun, and call another outer suspend fun, works well
        Unit.let {
            anotherOuterSuspendFun()
        }

        //case 5: use inline fun, and call another inner suspend fun, works well
        Unit.let {
            anotherInnerSuspendFun()
        }
    }
    doInnerSuspend()
}
