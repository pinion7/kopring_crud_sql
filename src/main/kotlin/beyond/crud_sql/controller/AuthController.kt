package beyond.crud_sql.controller

import beyond.crud_sql.dto.request.LoginRequestDto
import beyond.crud_sql.dto.response.ResponseDto
import beyond.crud_sql.dto.result.GetLoginResultDto
import beyond.crud_sql.dto.result.GetUserResultDto
import beyond.crud_sql.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val userService: UserService
) {
    private val log = LoggerFactory.getLogger(UserService::class.java)

    @PostMapping("/login")
    fun loginUser(@RequestBody @Validated request: LoginRequestDto): ResponseEntity<ResponseDto<GetLoginResultDto>> {
        val results = userService.getUserAndToken(request.email, request.password)
        return ResponseEntity.status(200).body(results)
    }

    @PostMapping("/logout")
    fun logoutUser(): String {
        return "success"
    }
}