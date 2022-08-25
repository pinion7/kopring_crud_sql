package beyond.crud_sql.repository

import beyond.crud_sql.dto.condition.PostSearchCondition
import beyond.crud_sql.dto.show.SearchPostShowDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface PostQuerydslRepository {
    fun findPostAllWithCondition(condition: PostSearchCondition, pageable: Pageable): Page<SearchPostShowDto>
}