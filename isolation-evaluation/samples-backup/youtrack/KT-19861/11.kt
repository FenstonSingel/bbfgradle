// Original bug: KT-22672
// Duplicated bug: KT-19861

class ValueContainer(var text: String)

class MainWindow {
    var container: ValueContainer? = null

    fun log(msg: String) {
        container?.text += msg
    }
}
