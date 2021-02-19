// Original bug: KT-37834
// Duplicated bug: KT-34919

interface InterfaceA : InterfaceB
interface InterfaceB: (a: String) -> Unit
