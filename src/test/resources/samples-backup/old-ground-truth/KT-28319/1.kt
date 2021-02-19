// Original bug: KT-28319
// Duplicated bug: KT-28319

interface SomeInterface
open class ParentClass<E : SomeInterface>(val member: E)
class ChildClass(sa: SomeInterface) : ParentClass<*>(sa)
