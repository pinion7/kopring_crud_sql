package beyond.crud_sql.common.custom

class ValidatorException(message: String?, val errors: MutableMap<String?, String?>) : RuntimeException(message)
