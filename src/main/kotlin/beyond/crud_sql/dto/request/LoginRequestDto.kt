package beyond.crud_sql.dto.request

import org.hibernate.validator.constraints.Length
import javax.validation.constraints.Email
import javax.validation.constraints.NotEmpty

data class LoginRequestDto(
    @field:NotEmpty(message = "필드 값이 유효하지 않습니다.")
    @field:Email(message = "올바른 email 형식이 아닙니다.")
    val email: String,

    @field:NotEmpty(message = "필드 값이 유효하지 않습니다.")
    @field:Length(min = 4, max = 12, message = "4자 이상 12자 이하여야 합니다.")
    val password: String,
)
