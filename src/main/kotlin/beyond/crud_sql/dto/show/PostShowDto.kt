package beyond.crud_sql.dto.show

import beyond.crud_sql.domain.Post
import com.querydsl.core.annotations.QueryProjection

class PostShowDto (post: Post) {
    val postId = post.id
    val title = post.title
    val content = post.content
    val createdDate = post.createdDate
    val lastModifiedDate = post.lastModifiedDate
}
