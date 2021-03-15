
fun cloneStorage() {
    suspend fun copyNs(from: MutableMap<Any, Any> ) {
        from.forEach {
            copyNs(TODO() )
        }
    }
}
