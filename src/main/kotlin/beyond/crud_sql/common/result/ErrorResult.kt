package beyond.crud_sql.common.result

open class ErrorResult(
    val error: String, val statusCode: Int, val message: String?, val cause: String?, open val validation: String? = null,
) {
}