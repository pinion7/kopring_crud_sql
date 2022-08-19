package beyond.crud_sql.common.result

class ValidatorErrorResult(
    error: String,
    statusCode: Int,
    message: String?,
    cause: String?,
    val validation: MutableMap<String?, String?>
) : ErrorResult(error, statusCode, message, cause)