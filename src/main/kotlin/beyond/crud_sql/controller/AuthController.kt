package beyond.crud_sql.controller

import beyond.crud_sql.common.aop.annotation.ValidationAop
import beyond.crud_sql.common.exception.result.InternalServerErrorResult
import beyond.crud_sql.common.exception.result.NotFoundErrorResult
import beyond.crud_sql.common.exception.result.ClassValidatorErrorResult
import beyond.crud_sql.common.exception.result.ErrorResult
import beyond.crud_sql.dto.request.LoginRequestDto
import beyond.crud_sql.dto.response.ResponseDto
import beyond.crud_sql.dto.result.GetLoginResultDto
import beyond.crud_sql.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@Tag(name = "Authentication", description = "인증 API")
@RestController
@RequestMapping("/auth")
@Validated
@ValidationAop
class AuthController(
    private val userService: UserService
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @PostMapping("/login")
    @Operation(summary = "로그인 API")
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "400", content = [Content(schema = Schema(implementation = ErrorResult::class))])
    @ApiResponse(responseCode = "404", content = [Content(schema = Schema(implementation = ErrorResult::class))])
    @ApiResponse(responseCode = "500", content = [Content(schema = Schema(implementation = ErrorResult::class))])
    fun loginUser(
        @RequestBody @Validated request: LoginRequestDto,
        bindingResult: BindingResult
    ): ResponseEntity<ResponseDto<GetLoginResultDto>> {
        val (email, password) = request
        val results = userService.getUserAndToken(email, password)
        return ResponseEntity.status(200).body(results)
    }
}