package beyond.crud_sql.common.exception.custom

class UnauthorizedException(message: String?, override val cause: Throwable?) : RuntimeException(message) {
    constructor(message: String?): this(message, null)

}