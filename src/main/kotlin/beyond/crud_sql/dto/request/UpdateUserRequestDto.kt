package beyond.crud_sql.dto.request

import org.hibernate.validator.constraints.Length
import javax.validation.constraints.NotEmpty

data class UpdateUserRequestDto(
    @field:Length(min = 36, max = 36, message = "UUID는 36자만 가능합니다.")
    var userId: String? = null,

    @field:NotEmpty(message = "필드 값이 유효하지 않습니다.")
    @field:Length(min = 2, max = 10, message = "2자 이상 10자 이하여야 합니다.")
    var nickname: String? = null
)
