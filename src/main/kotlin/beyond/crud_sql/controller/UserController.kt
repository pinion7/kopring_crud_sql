package beyond.crud_sql.controller

import beyond.crud_sql.common.result.ErrorResult
import beyond.crud_sql.common.result.InternalServerErrorResult
import beyond.crud_sql.dto.CreateUserDto
import beyond.crud_sql.dto.CreateUserResultDto
import beyond.crud_sql.dto.response.ResponseDto
import beyond.crud_sql.service.UserService
import org.postgresql.util.PSQLException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.HttpServerErrorException.InternalServerError
import org.springframework.web.context.request.WebRequest
import java.sql.SQLException
import java.util.*

@RestController
@RequestMapping("/users")
class UserController(val userService: UserService) {

    @PostMapping
    fun createUser(@RequestBody @Validated request: CreateUserDto): ResponseEntity<Any> {
        val results = userService.createUser(request)
        return ResponseEntity.status(201).body(results)
    }

//    @ExceptionHandler(SQLException::class)
//    protected fun handleSQLException(e: PSQLException) : ResponseEntity<ErrorResult> {
//        val errorResult: ErrorResult = InternalServerErrorResult("Internal Server Error", 500, "이메일 혹은 닉네임 중복입니다.")
//        return ResponseEntity<ErrorResult>(errorResult, HttpStatus.INTERNAL_SERVER_ERROR)
//    }
}