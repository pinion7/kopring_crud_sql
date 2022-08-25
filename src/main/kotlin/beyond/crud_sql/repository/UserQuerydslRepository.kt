package beyond.crud_sql.repository

import beyond.crud_sql.dto.condition.UserSearchCondition
import beyond.crud_sql.dto.show.SearchUserShowDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface UserQuerydslRepository {
    fun findUserAllWithCondition(condition: UserSearchCondition, pageable: Pageable): Page<SearchUserShowDto>
}