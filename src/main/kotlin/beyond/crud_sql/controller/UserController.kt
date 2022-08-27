package beyond.crud_sql.controller

import beyond.crud_sql.common.aop.annotation.ValidationAop
import beyond.crud_sql.common.exception.custom.ConflictException
import beyond.crud_sql.common.exception.result.*
import beyond.crud_sql.common.resolver.annotation.GetUser
import beyond.crud_sql.domain.User
import beyond.crud_sql.dto.condition.UserSearchCondition
import beyond.crud_sql.dto.request.CreateUserRequestDto
import beyond.crud_sql.dto.request.UpdateUserRequestDto
import beyond.crud_sql.dto.response.ResponseDto
import beyond.crud_sql.dto.result.*
import beyond.crud_sql.service.PostService
import beyond.crud_sql.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.hibernate.validator.constraints.Length
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.validation.constraints.Min

@Tag(name = "Users", description = "회원 API")
@RestController
@RequestMapping("/users")
@ValidationAop
@Validated
class UserController(
    private val userService: UserService,
    private val postService: PostService,
) {

    private val log = LoggerFactory.getLogger(javaClass)

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
            val results = userService.createUser(email, password, nickname)
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
    @ApiResponse(responseCode = "401", content = [Content(schema = Schema(implementation = ErrorResult::class))])
    @ApiResponse(responseCode = "404", content = [Content(schema = Schema(implementation = ErrorResult::class))])
    @ApiResponse(responseCode = "409", content = [Content(schema = Schema(implementation = ErrorResult::class))])
    @ApiResponse(responseCode = "500", content = [Content(schema = Schema(implementation = ErrorResult::class))])
    fun updateUser(
        @GetUser user: User,
        @RequestBody @Validated request: UpdateUserRequestDto,
        bindingResult: BindingResult,
    ): ResponseEntity<ResponseDto<UpdateUserResultDto>> {
        try {
            val results = userService.updateUser(user, request.nickname)
            return ResponseEntity.status(200).body(results)
        } catch (e: DataIntegrityViolationException) {
            throw ConflictException("닉네임 중복입니다.", e.cause)
        }
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "회원 탈퇴 API")
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "400", content = [Content(schema = Schema(implementation = ErrorResult::class))])
    @ApiResponse(responseCode = "401", content = [Content(schema = Schema(implementation = ErrorResult::class))])
    @ApiResponse(responseCode = "404", content = [Content(schema = Schema(implementation = ErrorResult::class))])
    @ApiResponse(responseCode = "500", content = [Content(schema = Schema(implementation = ErrorResult::class))])
    fun deleteUser(
        @GetUser user: User,
        @PathVariable @Length(min = 36, max = 36, message = "UUID는 36자만 가능합니다.") userId: String,
    ): ResponseEntity<ResponseDto<DeleteUserResultDto>> {
        val results = userService.deleteUser(user)
        return ResponseEntity.status(200).body(results)
    }

    @GetMapping("/{userId}/posts")
    @Operation(summary = "단일 회원 게시글 목록 조회 API")
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "400", content = [Content(schema = Schema(implementation = ErrorResult::class))])
    @ApiResponse(responseCode = "500", content = [Content(schema = Schema(implementation = ErrorResult::class))])
    fun getUserWithPostAll(
        @PathVariable @Length(min = 36, max = 36, message = "UUID는 36자만 가능합니다.") userId: String,
        @RequestParam("page") @Min(0, message = "0 이상이어야 합니다.") page: Int? = null,
        @RequestParam("size") @Min(1, message = "1 이상이어야 합니다.") size: Int? = null,
    ): ResponseEntity<ResponseDto<GetUserWithPostAllResultDto>> {
        val pageRequest = PageRequest.of(
            page ?: 0, size ?: 10, Sort.by(Sort.Direction.DESC, "createdDate")
        )
        val results = postService.getUserWithPostAll(UUID.fromString(userId), pageRequest)
        return ResponseEntity.status(200).body(results)
    }

    @GetMapping("/search")
    @Operation(summary = "회원 검색 API")
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "400", content = [Content(schema = Schema(implementation = ErrorResult::class))])
    @ApiResponse(responseCode = "500", content = [Content(schema = Schema(implementation = ErrorResult::class))])
    fun searchUserAll(
        condition: UserSearchCondition,
        @RequestParam("page") @Min(0, message = "0 이상이어야 합니다.") page: Int? = null,
        @RequestParam("size") @Min(1, message = "1 이상이어야 합니다.") size: Int? = null,
    ): ResponseEntity<ResponseDto<SearchUserAllResultDto>> {
        val pageRequest = PageRequest.of(page ?: 0, size ?: 10)
        val results = userService.searchUserAll(condition, pageRequest)
        return ResponseEntity.status(200).body(results)
    }
}