
tailrec fun findBreadcrumbPath(
        appGroup: Int?
        ): List<String> {
val parentGroup = mapOf( 1 to 
1,1 to 
1,1 to 
1,1 to 1  )[appGroup]
        Int?.let {
            return findBreadcrumbPath(parentGroup  )
        }
}
