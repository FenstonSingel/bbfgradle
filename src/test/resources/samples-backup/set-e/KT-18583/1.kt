// Parent bug: KT-18583

sealed class Result<T> {
    data class Success<T>(val value: T) : Result<T>()
    data class Failure(val exception: Throwable) : Result<Nothing>()

    fun getOrThrow() = when (this) {
        is Success -> value
        is Failure -> throw exception
    }
}
