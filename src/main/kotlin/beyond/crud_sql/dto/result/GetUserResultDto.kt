package beyond.crud_sql.dto.result

import java.time.LocalDateTime
import java.util.*

data class GetUserResultDto(
    val userId: UUID,
    val email: String,
    val nickname: String,
    val quit: Boolean,
    val createdDate: String?,
    val lastModifiedDate: String?,
)
