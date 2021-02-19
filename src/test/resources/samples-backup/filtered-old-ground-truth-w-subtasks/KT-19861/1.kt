// Original bug: KT-38926
// Duplicated bug: KT-19861

fun main() {
    class TestClass(var subtitle: String)
    var testClass: TestClass? = TestClass(subtitle = "")
    testClass?.subtitle += " "
}
