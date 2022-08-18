package beyond.crud_sql.common.custom

class NotFoundException(message: String?, override val cause: Throwable?) : RuntimeException(message) {
    constructor(message: String?): this(message, null)
}