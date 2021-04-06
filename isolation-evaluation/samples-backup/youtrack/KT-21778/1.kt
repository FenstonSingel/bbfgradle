// Original bug: KT-40332
// Duplicated bug: KT-21778

object Some {
    private val io1 = object {
        inline operator fun invoke() {}
    }

    private fun caller() {
        io1()
    }
}
