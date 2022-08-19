package beyond.crud_sql.common.aop

import beyond.crud_sql.common.custom.ValidatorException
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.validation.BindingResult

@Aspect
@Component
class BindingAspect {

    private val log = LoggerFactory.getLogger(BindingAspect::class.java)

//    @Pointcut("@annotation(beyond.crud_sql.common.aop.annotation.BindingAop)")
//    fun cut() {}
    @Pointcut("execution(* beyond.crud_sql.controller..*(..))")
    fun cut() {}

    @Before("cut()")
    fun doBinding(joinPoint: JoinPoint) {
        val args = joinPoint.args
        log.info("[trace] {} args={}", joinPoint.signature, args)

        for (arg in args) {
            if (arg is BindingResult) {
                if (arg.allErrors.size > 0) {
                    val errors = mutableMapOf<String?, String?>()
                    for (e in arg.allErrors) {
                        errors[e.code] = e.defaultMessage
                    }
                    throw ValidatorException("유효성 검사 에러입니다.", errors)
                }
            }
        }
    }
}