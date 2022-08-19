package beyond.crud_sql.dto.request

import org.hibernate.validator.constraints.Length
import org.hibernate.validator.constraints.Range
import javax.validation.constraints.Email
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class CreateUserRequestDto(
    @field:NotEmpty(message = "email 필드가 필요합니다.")
    @field:NotNull(message = "null일 수 없습니다.")
    @field:Email(message = "올바른 email 형식이 아닙니다.")
    val email: String,

    @field:NotEmpty(message = "password 필드가 필요합니다.")
    @field:NotNull(message = "null일 수 없습니다.")
    @field:Length(min = 4, max = 12, message = "4자 이상 12자 이하여야 합니다.")
    val password: String,

    @field:NotEmpty(message = "nickname 필드가 필요합니다.")
    @field:NotNull(message = "null일 수 없습니다.")
    @field:Length(min = 2, max = 10, message = "2자 이상 10자 이하여야 합니다.")
    val nickname: String
) {
}
