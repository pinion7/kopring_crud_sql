package beyond.crud_sql.common.exception.result

import io.swagger.v3.oas.annotations.media.Schema

class BadRequestErrorResult(
    @Schema(defaultValue = "ddd")
    error: String,
    statusCode: Int,
    message: String?,
    cause: String?
) : ErrorResult(error, statusCode, message, cause)