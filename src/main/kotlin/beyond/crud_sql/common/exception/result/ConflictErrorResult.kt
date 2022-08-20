package beyond.crud_sql.common.exception.result

class ConflictErrorResult(
    error: String,
    statusCode: Int,
    message: String?,
    cause: String?,
) : ErrorResult(error, statusCode, message, cause)