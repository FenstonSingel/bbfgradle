
class CrashMe1 {
    fun outString() = ""
    fun crashMe() {
let {
            class LocalLocal {
                val v = outString()
            }
            LocalLocal()
        }
    }
}
