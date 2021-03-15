// Original bug: KT-9552
// Duplicated bug: KT-9552

const val A = "A"
const val B = A+A+A+A+A+A+A+A+A+A+A+A+A+A+A+A+A+A+A+A+A+A+A+A+A+A+A;
const val C = B+B+B+B+B+B+B+B+B+B+B+B+B+B+B+B+B+B+B+B+B+B+B+B+B+B+B;
const val D = C+C+C+C+C+C+C+C+C+C+C+C+C+C+C+C+C+C+C+C+C+C+C+C+C+C+C;
const val E = D+D+D+D+D+D+D+D+D+D+D+D+D+D+D+D+D+D+D+D+D+D+D+D+D+D+D;

@JvmName(E)
fun foo(){ }
fun main(args: Array<String>) = foo()
