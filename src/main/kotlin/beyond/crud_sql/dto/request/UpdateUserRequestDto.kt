package beyond.crud_sql.dto.request

import org.hibernate.validator.constraints.Length
import javax.validation.constraints.NotEmpty

data class UpdateUserRequestDto(
    @field:NotEmpty(message = "필드 값이 유효하지 않습니다.")
    @field:Length(min = 2, max = 10, message = "2자 이상 10자 이하여야 합니다.")
    val nickname: String? = null
)
