// Original bug: KT-28499
// Duplicated bug: KT-35725

inline class InlineClass(val x: Int) {
    companion object {
        val superHashCode = super.hashCode()
//        val superToString = super.toString()  // the same for
//        val superEquals = super.equals(Any()) // two others calls
    }
}
