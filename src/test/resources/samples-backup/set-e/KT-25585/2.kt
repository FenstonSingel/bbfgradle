// Parent bug: KT-25585

fun id(n: Int) = n
var f: (Int) -> Int = ::id

fun main(args: Array<String>) {
    run {
        f = if (true) ::id else ::id
    }
}
