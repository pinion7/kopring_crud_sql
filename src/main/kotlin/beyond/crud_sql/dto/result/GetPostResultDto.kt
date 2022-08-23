package beyond.crud_sql.dto.result

import java.time.LocalDateTime
import java.util.*

data class GetPostResultDto(
    val postId: UUID,
    val userId: UUID,
    val writer: String,
    val title: String,
    val content: String,
    val createdDate: LocalDateTime,
    val lastModifiedDate: LocalDateTime,
)