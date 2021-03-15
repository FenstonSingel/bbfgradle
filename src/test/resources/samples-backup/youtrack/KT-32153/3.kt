// Original bug: KT-39507
// Duplicated bug: KT-32153

suspend fun fetchAllPages(): PaginatedVO {
    suspend fun PaginatedVO.getNextPages(): PaginatedVO {
        return this.let {
            this.getNextPages()
        }
    }
    return fetchData().getNextPages()
}

fun fetchData(): PaginatedVO {
    return PaginatedVO()
}

class PaginatedVO
