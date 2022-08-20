package beyond.crud_sql.common.exception.result

class ClassValidatorErrorResult(
    error: String,
    statusCode: Int,
    message: String?,
    val validation: MutableMap<String?, String?>,
) : ErrorResult(error, statusCode, message)