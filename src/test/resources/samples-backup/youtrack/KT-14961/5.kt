// Original bug: KT-28570
// Duplicated bug: KT-14961

tailrec fun findBreadcrumbPath(
        appGroup: Int?,
        groupsMap: Map<Int, Int>,
        path: List<String> = emptyList()
): List<String> {
    if (appGroup != null) {
        val parentGroup = groupsMap[appGroup]
        parentGroup?.let {
            return findBreadcrumbPath(parentGroup, groupsMap, path + it.toString())
        }
    }

    return path
}

fun main() {
    findBreadcrumbPath(0, emptyMap(), emptyList())
}
