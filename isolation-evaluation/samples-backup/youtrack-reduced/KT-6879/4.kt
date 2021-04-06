
class X {
    abstract inner class Y 
}
fun yy() = with(TODO(),{ object : X.Y() {} })
