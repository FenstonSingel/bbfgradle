// Original bug: KT-17470
// Duplicated bug: KT-9552

const val s0 = "abcdef0123456789"
const val s1 = s0 + s0 + s0 + s0 + s0 + s0 + s0 + s0
const val s2 = s1 + s1 + s1 + s1 + s1 + s1 + s1 + s1
const val s3 = s2 + s2 + s2 + s2 + s2 + s2 + s2 + s2
const val s4 = s3 + s3 + s3 + s3 + s3 + s3 + s3 + s3

class A{
    @JvmName(s4)
    fun foo () {}
}
