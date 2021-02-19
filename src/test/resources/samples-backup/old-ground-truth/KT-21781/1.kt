// Original bug: KT-24572
// Duplicated bug: KT-21781

open class UITapGestureRecognizer(fn: () -> Unit) {}
abstract class CView {
    var cView: CView? = null
        set(value) {
            value?.let { v ->
                object : UITapGestureRecognizer({ this }) {}
            }
        }
}
