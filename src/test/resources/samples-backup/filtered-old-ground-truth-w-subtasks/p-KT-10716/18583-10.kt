// Original bug: KT-18583
// Duplicated bug: KT-18583

sealed class Permit<K> {
    companion object {
        operator fun <K> invoke(lookup : K, permission : Permissions) = FailedPermit(lookup, permission)
    }
    class FailedPermit<K>(val lookup : K, val permission : Permissions) : Permit<K>()
    fun orElse(closure : (K, Permissions) -> Unit) = when (this) {         //Dies on this line
        is FailedPermit -> closure(lookup, permission)
        else -> {}
    }
}
