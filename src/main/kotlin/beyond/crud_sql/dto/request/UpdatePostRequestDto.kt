package beyond.crud_sql.dto.request

import org.hibernate.validator.constraints.Length

data class UpdatePostRequestDto(
    @field:Length(min = 1, message = "1자 이상이어야 합니다.")
    val title: String? = null,

    @field:Length(min = 1, message = "1자 이상이어야 합니다.")
    val content: String? = null
)
