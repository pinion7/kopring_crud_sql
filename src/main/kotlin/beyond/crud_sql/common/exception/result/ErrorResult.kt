package beyond.crud_sql.common.exception.result

import io.swagger.v3.oas.annotations.media.Schema

open class ErrorResult(
    val error: String, val statusCode: Int, val message: String?, val cause: String?,
) {
    constructor(error: String, statusCode: Int, message: String?) : this(error, statusCode, message, null)

    override fun toString(): String {
        return "ErrorResult(error='$error', statusCode=$statusCode, message=$message, cause=$cause)"
    }
}
