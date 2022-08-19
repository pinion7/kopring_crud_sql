package beyond.crud_sql.common.advice

import beyond.crud_sql.common.custom.ValidatorException
import beyond.crud_sql.common.custom.ConflictException
import beyond.crud_sql.common.custom.NotFoundException
import beyond.crud_sql.common.custom.UnauthorizedException
import beyond.crud_sql.common.result.*
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ExControllerAdvice {
    private val log = LoggerFactory.getLogger(ExControllerAdvice::class.java)

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

    @ExceptionHandler(ValidatorException::class)
    fun validatorExHandler(e: ValidatorException): ResponseEntity<ErrorResult> {
        val errorResult: ErrorResult = ValidatorErrorResult(
            "Invalid Request",
            400,
            e.message,
            e.cause?.cause?.localizedMessage,
            e.errors
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
    fun internalServerExHandler(e: Exception): ResponseEntity<InternalServerErrorResult> {
        log.error(e.localizedMessage)
        val errorResult = InternalServerErrorResult(
            "Internal Server Error",
            500,
            e.message,
            e.cause?.cause?.localizedMessage
        )
        log.info(errorResult.toString())
        return ResponseEntity<InternalServerErrorResult>(errorResult, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}