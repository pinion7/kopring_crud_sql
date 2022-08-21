package beyond.crud_sql.controller

import beyond.crud_sql.common.aop.annotation.ValidationAop
import beyond.crud_sql.common.exception.custom.ConflictException
import beyond.crud_sql.common.exception.result.*
import beyond.crud_sql.dto.request.CreateUserRequestDto
import beyond.crud_sql.dto.request.UpdateUserRequestDto
import beyond.crud_sql.dto.response.ResponseDto
import beyond.crud_sql.dto.result.CreateUserResultDto
import beyond.crud_sql.dto.result.DeleteUserResultDto
import beyond.crud_sql.dto.result.GetUserResultDto
import beyond.crud_sql.dto.result.UpdateUserResultDto
import beyond.crud_sql.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.hibernate.validator.constraints.Length
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.ResponseEntity
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.BindingErrorProcessor
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.util.*

@Tag(name = "Users", description = "회원 API")
@RestController
@RequestMapping("/users")
@ValidationAop
@Validated
class UserController(
    private val userService: UserService,
) {

    private val log = LoggerFactory.getLogger(UserController::class.java)

    @PostMapping
    @Operation(summary = "회원 가입 API")
    @ApiResponse(responseCode = "201")
    @ApiResponse(responseCode = "400", content = [Content(schema = Schema(implementation = ErrorResult::class))])
    @ApiResponse(responseCode = "409", content = [Content(schema = Schema(implementation = ErrorResult::class))])
    @ApiResponse(responseCode = "500", content = [Content(schema = Schema(implementation = ErrorResult::class))])
    fun createUser(
        @RequestBody @Validated request: CreateUserRequestDto,
        bindingResult: BindingResult,
    ): ResponseEntity<ResponseDto<CreateUserResultDto>> {
        try {
            val (email, password, nickname) = request
            val results = userService.createUser(email!!, password!!, nickname!!)
            return ResponseEntity.status(201).body(results)
        } catch (e: DataIntegrityViolationException) {
            throw ConflictException("이메일 혹은 닉네임 중복입니다.", e.cause)
        }
    }

    @GetMapping("/{userId}")
    @Operation(summary = "회원 조회 API")
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "400", content = [Content(schema = Schema(implementation = ErrorResult::class))])
    @ApiResponse(responseCode = "404", content = [Content(schema = Schema(implementation = ErrorResult::class))])
    @ApiResponse(responseCode = "500", content = [Content(schema = Schema(implementation = ErrorResult::class))])
    fun getUser(
        @PathVariable @Length(min = 36, max = 36, message = "UUID는 36자만 가능합니다.") userId: String,
    ): ResponseEntity<ResponseDto<GetUserResultDto>> {
        val results = userService.getUser(UUID.fromString(userId))
        return ResponseEntity.status(200).body(results)
    }

    @PatchMapping("/{userId}")
    @Operation(summary = "회원 수정 API")
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "400", content = [Content(schema = Schema(implementation = ErrorResult::class))])
    @ApiResponse(responseCode = "404", content = [Content(schema = Schema(implementation = ErrorResult::class))])
    @ApiResponse(responseCode = "409", content = [Content(schema = Schema(implementation = ErrorResult::class))])
    @ApiResponse(responseCode = "500", content = [Content(schema = Schema(implementation = ErrorResult::class))])
    fun updateUser(
        @RequestBody @Validated request: UpdateUserRequestDto,
        bindingResult: BindingResult,
    ): ResponseEntity<ResponseDto<UpdateUserResultDto>> {
        try {
            val (userId, nickname) = request
            val results = userService.updateUser(UUID.fromString(userId), nickname!!)
            return ResponseEntity.status(200).body(results)
        } catch (e: DataIntegrityViolationException) {
            throw ConflictException("닉네임 중복입니다.", e.cause)
        }
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "회원 삭제 API")
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "400", content = [Content(schema = Schema(implementation = ErrorResult::class))])
    @ApiResponse(responseCode = "500", content = [Content(schema = Schema(implementation = ErrorResult::class))])
    fun deleteUser(
        @PathVariable @Length(min = 36, max = 36, message = "UUID는 36자만 가능합니다.") userId: String,
    ): ResponseEntity<ResponseDto<DeleteUserResultDto>> {
        val results = userService.deleteUser(UUID.fromString(userId))
        return ResponseEntity.status(200).body(results)
    }
}