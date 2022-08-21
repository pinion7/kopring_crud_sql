package beyond.crud_sql.common.exception.custom

class ClassValidatorException(message: String?, val errors: MutableMap<String?, MutableList<String>?>) : RuntimeException(message)
