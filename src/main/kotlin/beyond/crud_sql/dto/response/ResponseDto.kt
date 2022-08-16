package beyond.crud_sql.dto.response

class ResponseDto <T> (
    val results: T,
    val statusCode: Int,
    val message: String
)