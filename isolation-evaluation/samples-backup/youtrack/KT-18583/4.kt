// Original bug: KT-28521
// Duplicated bug: KT-18583

// FOO
package com.jetbrains.python

//FOO
public sealed class PyResult<T_OK, T_ERR> {


  data class Error<T_OK, T_ERR>(val error: T_ERR) : PyResult<T_OK, T_ERR>()
  data class OK<T_OK, T_ERR>(val result: T_OK) : PyResult<T_OK, T_ERR>()


  fun <R> map(onError: (T_ERR) -> R, onOK: (T_OK) -> R) =
    when (this) {
      is OK -> onOk(result)
    }
}
