package beyond.crud_sql.dto.result

import beyond.crud_sql.dto.show.SearchUserShowDto
import org.springframework.data.domain.Page

class SearchUserAllResultDto(
    pageableUser: Page<SearchUserShowDto>
) {
    val users = pageableUser.content
    val totalPages = pageableUser.totalPages
    val totalElements = pageableUser.totalElements
    val numberOfElements = pageableUser.numberOfElements
    val pageNumber = pageableUser.number
    val pageSize = pageableUser.size
    val isFirst = pageableUser.isFirst
    val isNext = pageableUser.hasNext()
}