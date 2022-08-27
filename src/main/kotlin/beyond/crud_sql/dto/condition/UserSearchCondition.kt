package beyond.crud_sql.dto.condition

data class UserSearchCondition(
    val email: String? = null,
    val nickname: String? = null,
)
