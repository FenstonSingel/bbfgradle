
fun outerSuspendFun() {
suspend fun doInnerSuspend() {
Unit.let {
            doInnerSuspend()
        }
}
}
