// Original bug: KT-38157
// Duplicated bug: KT-34919

package com.example

typealias Alfa<T, R> = (arg: T) -> R?

interface Beta {
    fun x();
}

interface Gamma<R> : Alfa<Beta, R>

fun <R> R.teta(a: Gamma<R>, t: Int) : R {
    return a(this)
}

