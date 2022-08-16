package beyond.crud_sql.common.advice

import beyond.crud_sql.common.custom.ConflictException
import beyond.crud_sql.common.custom.NotFoundException
import beyond.crud_sql.common.result.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindException
import org.springframework.web.bind.MissingRequestValueException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import javax.validation.ValidationException

@RestControllerAdvice
class ExControllerAdvice {
    @ExceptionHandler
    fun badRequestExHandler(e: IllegalArgumentException): ResponseEntity<ErrorResult> {
        val errorResult: ErrorResult = BadRequestErrorResult("Bad Request", 400, e.message)
        return ResponseEntity<ErrorResult>(errorResult, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(ValidationException::class, BindException::class, MissingRequestValueException::class)
    fun validationExHandler(e: Exception): ResponseEntity<ErrorResult> {
        val errorResult: ErrorResult = ValidatorErrorResult("Invalid Request", 400, "유효성 검사 에러 입니다.", e.message)
        return ResponseEntity<ErrorResult>(errorResult, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler
    fun notFoundExHandler(e: NotFoundException): ResponseEntity<ErrorResult> {
        val errorResult: ErrorResult = NotFoundErrorResult("Not Found", 404, e.message)
        return ResponseEntity<ErrorResult>(errorResult, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler
    fun conflictExHandler(e: ConflictException): ResponseEntity<ErrorResult> {
        val errorResult: ErrorResult = ConflictErrorResult("Conflict", 409, e.message)
        return ResponseEntity<ErrorResult>(errorResult, HttpStatus.CONFLICT)
    }

    @ExceptionHandler
    fun internalServerExHandler(e: Exception): ResponseEntity<ErrorResult> {
        val errorResult: ErrorResult = InternalServerErrorResult("Internal Server Error", 500, e.message)
        return ResponseEntity<ErrorResult>(errorResult, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}