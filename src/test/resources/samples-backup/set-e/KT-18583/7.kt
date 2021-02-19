// Parent bug: KT-18583

    sealed class Maybe<T> {
        class Nope<T>(val reasonForLog: String, val reasonForUI: String) : Maybe<T>()
        class Yeah<T>(val meat: T) : Maybe<T>()

        // fun unwrap(): T = when (this) {    // <-- With `: T` it's OK

        fun unwrap() = when (this) {          // <-- Error: Kotlin: [Internal Error]
            is Nope -> throw Exception(reasonForLog)
            is Yeah -> meat
        }
    }
