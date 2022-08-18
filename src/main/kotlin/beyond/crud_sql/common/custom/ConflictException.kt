package beyond.crud_sql.common.custom

class ConflictException(message: String?, override val cause: Throwable?) : RuntimeException(message)