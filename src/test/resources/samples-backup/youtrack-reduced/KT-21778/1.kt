
private val io1 = object {
        inline operator fun invoke():Unit = TODO()
    }
fun caller() {
        io1()
    }
