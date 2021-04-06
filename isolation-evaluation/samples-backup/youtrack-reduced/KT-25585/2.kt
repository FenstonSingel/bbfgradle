
fun id() = TODO()
var f: () -> Int =
run {
if (true) 
id else ::id
    }
