package beyond.crud_sql.controller

import beyond.crud_sql.dto.request.CreateUserRequestDto
import beyond.crud_sql.dto.response.ResponseDto
import beyond.crud_sql.dto.result.CreateUserResultDto
import beyond.crud_sql.dto.result.GetUserResultDto
import beyond.crud_sql.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.util.UUID
import javax.validation.constraints.Min

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService,
) {

    private val log = LoggerFactory.getLogger(UserService::class.java)

    @PostMapping
    fun createUser(@RequestBody @Validated request: CreateUserRequestDto): ResponseEntity<ResponseDto<CreateUserResultDto>> {
        val results = userService.createUser(request)
        return ResponseEntity.status(201).body(results)
    }

    @GetMapping("/{userId}")
    fun getUser(@PathVariable @Min(1) userId: UUID): ResponseEntity<ResponseDto<GetUserResultDto>> {
        val results = userService.getUser(userId)
        return ResponseEntity.status(200).body(results)
    }
}