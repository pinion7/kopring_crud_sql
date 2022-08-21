package beyond.crud_sql.common.exception.result

class ParamValidatorErrorResult(
    error: String,
    statusCode: Int,
    message: String? = "유효성 검사 에러입니다.",
    val validation: MutableMap<String?, MutableList<String>?>,
) : ErrorResult(error, statusCode, message)