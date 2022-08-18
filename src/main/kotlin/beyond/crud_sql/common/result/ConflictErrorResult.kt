package beyond.crud_sql.common.result

class ConflictErrorResult(
    error: String,
    statusCode: Int,
    message: String?,
    cause: String?,
    override val validation: String? = null
) : ErrorResult(error, statusCode, message, cause)