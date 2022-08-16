package beyond.crud_sql.dto

import javax.validation.constraints.Email
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class CreateUserDto(
    @NotEmpty
    @NotNull
    @Email
    val email: String,

    @NotEmpty
    @NotNull
    val password: String,

    @NotEmpty
    @NotNull
    val nickname: String
) {
}
