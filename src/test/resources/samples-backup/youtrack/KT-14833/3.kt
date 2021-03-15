// Original bug: KT-14833
// Duplicated bug: KT-14833

import kotlin.properties.*
import kotlin.reflect.KProperty

fun main() {
    var blah: Int by Delegates.blah(
        initialValue = 0,
        validate = { property, oldValue, newValue ->
            if (newValue < 0) {
                println("validation failed: ${property.name} >= 0")
                return@blah false
            }
            return@blah true
        },
        onChange = { property, oldValue, newValue ->
            println("onChange: ${property.name} from $oldValue to $newValue")
        }
    )

    blah += 1
//    blah = blah + 1
}

public inline fun <T> Delegates.blah(
    initialValue: T,
    crossinline validate: (property: KProperty<*>, oldValue: T, newValue: T) -> Boolean,
    crossinline onChange: (property: KProperty<*>, oldValue: T, newValue: T) -> Unit
): ReadWriteProperty<Any?, T> = object : ObservableProperty<T>(initialValue) {

    override fun beforeChange(property: KProperty<*>, oldValue: T, newValue: T): Boolean {
        return validate(property, oldValue, newValue)
    }

    override fun afterChange(property: KProperty<*>, oldValue: T, newValue: T) {
        return onChange(property, oldValue, newValue)
    }

}
