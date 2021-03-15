// Original bug: KT-28022
// Duplicated bug: KT-8199

fun String.testMe(someOtherValue: String) {
    data class TestMe(
        val doesThisWork: String = this,
        val somethingElse: String = someOtherValue
    )
}
