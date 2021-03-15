// Original bug: KT-13596
// Duplicated bug: KT-13596

open class A<T>

class B : A<B.X>() {
    typealias X = Any
}
