// Parent bug: KT-18583

sealed class Result {
  object Success : Result()
  data class Failure(val exception: RuntimeException) : Result()

  fun <T> invoke(callback: Callback<T>) = when (this) {
    is Result.Success -> callback.onSuccess(this)
    is Result.Failure -> callback.onFailure(this)
  }

  interface Callback<T> {
    fun onSuccess(success: Result.Success) : T
    fun onFailure(failure: Result.Failure): T
  }
}
