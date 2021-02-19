// Original bug: KT-21778
// Duplicated bug: KT-21778

object Some {
    private val io1 = object {
        inline operator fun invoke() {}
    }

    private fun caller() {
        io1()
    }
}
