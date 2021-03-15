// Original bug: KT-23425
// Duplicated bug: KT-13306

class CrashMe2 {
    fun outString() = ""
    fun crashMe() {
        "abc".let {
            class LocalLocal {
                val v = outString()
            }
            LocalLocal()
        }
    }
}
