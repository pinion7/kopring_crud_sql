package beyond.crud_sql.dto.result

import java.time.LocalDateTime
import java.util.UUID

data class GetLoginResultDto(
    val userId: UUID,
    val email: String,
    val nickname: String,
    val createdDate: String?,
    val lastModifiedDate: String?,
    val accessToken: String
) {
}