package beyond.crud_sql.common.advice

import beyond.crud_sql.common.aop.annotation.ValidationAop
import beyond.crud_sql.common.exception.custom.ConflictException
import beyond.crud_sql.common.exception.custom.NotFoundException
import beyond.crud_sql.common.exception.custom.UnauthorizedException
import beyond.crud_sql.common.exception.custom.ClassValidatorException
import beyond.crud_sql.common.exception.result.*
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import javax.validation.ConstraintViolationException

@RestControllerAdvice
class ExControllerAdvice {
    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler
    fun badRequestExHandler(e: IllegalArgumentException): ResponseEntity<ErrorResult> {
        val errorResult: ErrorResult = BadRequestErrorResult(
            "Bad Request",
            400,
            e.message,
            e.cause?.cause?.localizedMessage
        )
        return ResponseEntity<ErrorResult>(errorResult, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler
    fun classValidatorExHandler(e: ClassValidatorException): ResponseEntity<ErrorResult> {
        val errorResult: ErrorResult = ClassValidatorErrorResult(
            "Invalid Request",
            400,
            e.message,
            e.errors
        )
        return ResponseEntity<ErrorResult>(errorResult, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(ConstraintViolationException::class, MethodArgumentTypeMismatchException::class)
    fun paramValidatorExHandler(e: Exception): ResponseEntity<ErrorResult> {
        val errors = mutableMapOf<String?, MutableList<String>?>()
        val messages = e.message?.split(", ")
        for (m in messages!!) {
            val (temp, value) = m.split(": ")
            val key = temp.split(".")[1]
            if (errors[key] == null) errors[key] = mutableListOf()
            errors[key]!!.add(value)
        }

        val errorResult: ErrorResult = ParamValidatorErrorResult(
            "Invalid Request",
            400,
            validation = errors
        )
        return ResponseEntity<ErrorResult>(errorResult, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler
    fun unAuthorizedExHandler(e: UnauthorizedException): ResponseEntity<ErrorResult> {
        val errorResult: ErrorResult = UnauthorizedErrorResult(
            "Unauthorized",
            401,
            e.message,
            e.cause?.cause?.localizedMessage,
        )
        return ResponseEntity<ErrorResult>(errorResult, HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler
    fun notFoundExHandler(e: NotFoundException): ResponseEntity<ErrorResult> {
        val errorResult: ErrorResult = NotFoundErrorResult(
            "Not Found",
            404,
            e.message,
            e.cause?.cause?.localizedMessage
        )
        return ResponseEntity<ErrorResult>(errorResult, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler
    fun conflictExHandler(e: ConflictException): ResponseEntity<ErrorResult> {
        val errorResult: ErrorResult = ConflictErrorResult(
            "Conflict",
            409,
            e.message,
            e.cause?.cause?.localizedMessage
        )
        return ResponseEntity<ErrorResult>(errorResult, HttpStatus.CONFLICT)
    }

    @ExceptionHandler
    fun internalServerExHandler(e: Exception): ResponseEntity<ErrorResult> {
        log.error(e.toString())
        val errorResult: ErrorResult = InternalServerErrorResult(
            "Internal Server Error",
            500,
            e.message,
            e.cause?.cause?.localizedMessage
        )
        return ResponseEntity<ErrorResult>(errorResult, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}