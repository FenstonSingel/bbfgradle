
inline fun <T> doSubmit(crossinline task: () -> T)  {
val newJob = { task.invoke() }
}
fun another(command: Runnable)  = doSchedule { command.run() }
inline fun <V : Any
> doSchedule(crossinline callable: () -> V)  {
{ doSubmit(callable) }
}
