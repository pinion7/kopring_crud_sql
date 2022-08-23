package beyond.crud_sql.dto.request

import org.hibernate.validator.constraints.Length
import javax.validation.constraints.NotEmpty

data class CreatePostRequestDto(
    @field:NotEmpty(message = "필드 값이 유효하지 않습니다.")
    @field:Length(min = 1, message = "1자 이상이어야 합니다.")
    var title: String,

    @field:NotEmpty(message = "필드 값이 유효하지 않습니다.")
    @field:Length(min = 1, message = "1자 이상이어야 합니다.")
    var content: String
)
