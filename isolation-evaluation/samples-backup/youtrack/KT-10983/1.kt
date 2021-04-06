// Original bug: KT-8454
// Duplicated bug: KT-10983

class A<T : @public({
    val z = 1 // Select 1 and Extract Function (Ctrl+Alt+M)
}) Any>
