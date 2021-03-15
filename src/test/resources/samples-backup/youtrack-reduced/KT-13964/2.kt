
interface MyInterface
class MyBaseClass
class Hello {
  companion object {
fun <T> invoke(t: T) where T : MyBaseClass, T : MyInterface = TODO()
  }
}
fun 
() {
  Hello(MyBaseClass())
}
