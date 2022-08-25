package beyond.crud_sql.dto.result

import beyond.crud_sql.domain.Post
import beyond.crud_sql.dto.show.SearchPostShowDto
import org.springframework.data.domain.Page

class GetPostAllResultDto(
    pageablePost: Page<Post>
) {
    val posts = pageablePost.content.map { post -> SearchPostShowDto(post) }
    val totalPages = pageablePost.totalPages
    val totalElements = pageablePost.totalElements
    val numberOfElements = pageablePost.numberOfElements
    val pageNumber = pageablePost.number
    val pageSize = pageablePost.size
    val isFirst = pageablePost.isFirst
    val isNext = pageablePost.hasNext()
}