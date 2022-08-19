package beyond.crud_sql.common.result

import io.swagger.v3.oas.annotations.media.Schema

open class ErrorResult(
    val error: String, val statusCode: Int, val message: String?, val cause: String?
) {
    override fun toString(): String {
        return "ErrorResult(error='$error', statusCode=$statusCode, message=$message, cause=$cause)"
    }
}
