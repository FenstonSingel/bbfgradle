
typealias Alfa<T, R> = (arg: T) -> R
interface Beta
interface Gamma<R> : Alfa<Beta, R>
fun
(a: Gamma<R> )   {
     a()
}
