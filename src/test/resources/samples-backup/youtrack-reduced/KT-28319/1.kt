
class ParentClass<E : SomeInterface>(
 member: E)
class ChildClass : ParentClass<*>()
