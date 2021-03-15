// Original bug: KT-21789
// Duplicated bug: KT-14833

import kotlin.properties.Delegates

fun main(args: Array<String>) {
    var k by Delegates.observable (0, { _, old, new -> println("Change from $old to $new")})
    k+=1
}
