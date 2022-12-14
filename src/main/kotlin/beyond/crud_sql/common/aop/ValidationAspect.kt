package beyond.crud_sql.common.aop

import beyond.crud_sql.common.exception.custom.ClassValidatorException
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.validation.BindingResult

@Aspect
@Component
class ValidationAspect {

    private val log = LoggerFactory.getLogger(javaClass)

    @Pointcut("@annotation(beyond.crud_sql.common.aop.annotation.ValidationAop)")
    fun cutMethod() {
    }

    @Pointcut("@within(beyond.crud_sql.common.aop.annotation.ValidationAop)")
    fun cutClass() {
    }

    @Before("cutMethod() || cutClass()")
    fun doValidation(joinPoint: JoinPoint) {
        val args = joinPoint.args
        log.info("[trace] {} args={}", joinPoint.signature, args)

        for (arg in args) {
            if (arg is BindingResult) {
                if (arg.fieldErrors.size > 0) {
                    val errors = mutableMapOf<String?, MutableList<String>?>()
                    for (e in arg.fieldErrors) {
                        if (errors[e.field] == null) errors[e.field] = mutableListOf()
                        errors[e.field]!!.add(e.defaultMessage!!)
                    }
                    throw ClassValidatorException("유효성 검사 에러입니다.", errors)
                }
            }
        }
    }
}