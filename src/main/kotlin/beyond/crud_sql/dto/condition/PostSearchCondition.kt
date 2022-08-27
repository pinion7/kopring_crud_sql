package beyond.crud_sql.dto.condition

data class PostSearchCondition(
    val writer: String? = null,
    val title: String? = null,
    val content: String? = null
)
