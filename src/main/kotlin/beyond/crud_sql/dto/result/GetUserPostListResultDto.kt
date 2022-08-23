package beyond.crud_sql.dto.result

import beyond.crud_sql.domain.Post
import beyond.crud_sql.dto.show.PostShowDto
import org.springframework.data.domain.Page
import java.util.UUID

class GetUserPostListResultDto(
    val userId: UUID,
    pageablePost: Page<Post>
) {
    val posts = pageablePost.content.map { post -> PostShowDto(post) }
    val totalPages = pageablePost.totalPages;
    val totalElements = pageablePost.totalElements
    val numberOfElements = pageablePost.numberOfElements;
    val pageNumber = pageablePost.number;
    val pageSize = pageablePost.size;
    val isFirst = pageablePost.isFirst;
    val isNext = pageablePost.hasNext();
}
