package beyond.crud_sql.common.result

class BadRequestErrorResult(
    error: String,
    statusCode: Int,
    message: String?,
    cause: String?
) : ErrorResult(error, statusCode, message, cause)