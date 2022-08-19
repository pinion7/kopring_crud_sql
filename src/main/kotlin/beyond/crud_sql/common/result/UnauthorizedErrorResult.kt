package beyond.crud_sql.common.result

class UnauthorizedErrorResult(
    error: String,
    statusCode: Int,
    message: String?,
    cause: String?,
) : ErrorResult(error, statusCode, message, cause)