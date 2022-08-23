package beyond.crud_sql.common.resolver.annotation

import io.swagger.v3.oas.annotations.Parameter

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Parameter(name = "user", required = false, hidden = true)
annotation class GetUser