// Original bug: KT-22239
// Duplicated bug: KT-22274

fun main(par:Array<String>){
	fun f(p:()->Unit) {p()}
	m@ f({return@m})
}
