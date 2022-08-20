package beyond.crud_sql.controller

import beyond.crud_sql.common.aop.annotation.ValidationAop
import beyond.crud_sql.common.exception.result.ClassValidatorErrorResult
import beyond.crud_sql.common.exception.result.ConflictErrorResult
import beyond.crud_sql.common.exception.result.InternalServerErrorResult
import beyond.crud_sql.common.exception.result.NotFoundErrorResult
import beyond.crud_sql.dto.request.CreateUserRequestDto
import beyond.crud_sql.dto.response.ResponseDto
import beyond.crud_sql.dto.result.CreateUserResultDto
import beyond.crud_sql.dto.result.GetUserResultDto
import beyond.crud_sql.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.hibernate.validator.constraints.Length
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.util.*

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
    @ApiResponse(responseCode = "400", content = [Content(schema = Schema(implementation = ClassValidatorErrorResult::class))])
    @ApiResponse(responseCode = "409", content = [Content(schema = Schema(implementation = ConflictErrorResult::class))])
    @ApiResponse(responseCode = "500", content = [Content(schema = Schema(implementation = InternalServerErrorResult::class))])
    fun createUser(
        @RequestBody @Validated request: CreateUserRequestDto,
        bindingResult: BindingResult
    ): ResponseEntity<ResponseDto<CreateUserResultDto>> {
        val results = userService.createUser(request)
        return ResponseEntity.status(201).body(results)
    }

    @GetMapping("/{userId}")
    @Operation(summary = "회원 조회 API")
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "404", content = [Content(schema = Schema(implementation = NotFoundErrorResult::class))])
    @ApiResponse(responseCode = "500", content = [Content(schema = Schema(implementation = InternalServerErrorResult::class))])
    fun getUser(
        @PathVariable @Length(min = 36, max = 36, message = "UUID는 36자만 가능합니다.") userId: String,
        bindingResult: BindingResult
    ): ResponseEntity<ResponseDto<GetUserResultDto>> {
        val results = userService.getUser(UUID.fromString(userId))
        return ResponseEntity.status(200).body(results)
    }
}