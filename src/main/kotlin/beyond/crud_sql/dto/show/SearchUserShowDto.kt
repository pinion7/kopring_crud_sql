package beyond.crud_sql.dto.show

import beyond.crud_sql.domain.User
import com.querydsl.core.annotations.QueryProjection

class SearchUserShowDto @QueryProjection constructor(user: User) {
    val userId = user.id
    val email = user.email
    val nickname = user.nickname
    val quit: Boolean = user.quit
    val createdDate = user.createdDate
    val lastModifiedDate = user.lastModifiedDate
    val posts = user.posts.map { PostShowDto(it) }
}