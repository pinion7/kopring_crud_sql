package beyond.crud_sql.repository

import beyond.crud_sql.domain.Post
import beyond.crud_sql.dto.condition.PostSearchCondition
import beyond.crud_sql.dto.show.PostShowDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface PostRepositoryCustom {

    fun findPostListWithCondition(condition: PostSearchCondition, pageable: Pageable): Page<PostShowDto>
}