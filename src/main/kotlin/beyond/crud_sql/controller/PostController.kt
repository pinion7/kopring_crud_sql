package beyond.crud_sql.controller

import beyond.crud_sql.common.aop.annotation.ValidationAop
import beyond.crud_sql.common.exception.result.ErrorResult
import beyond.crud_sql.common.resolver.annotation.GetUser
import beyond.crud_sql.domain.User
import beyond.crud_sql.dto.request.CreatePostRequestDto
import beyond.crud_sql.dto.request.UpdatePostRequestDto
import beyond.crud_sql.dto.response.ResponseDto
import beyond.crud_sql.dto.result.CreatePostResultDto
import beyond.crud_sql.dto.result.DeletePostResultDto
import beyond.crud_sql.dto.result.GetPostResultDto
import beyond.crud_sql.dto.result.UpdatePostResultDto
import beyond.crud_sql.service.PostService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.hibernate.validator.constraints.Length
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.util.UUID

@Tag(name = "Posts", description = "게시판 API")
@RestController
@RequestMapping("/posts")
@ValidationAop
@Validated
class PostController(
    private val postService: PostService,
) {
    private val log = LoggerFactory.getLogger(PostController::class.java)

    @PostMapping
    @Operation(summary = "게시글 작성 API")
    @ApiResponse(responseCode = "201")
    @ApiResponse(responseCode = "400", content = [Content(schema = Schema(implementation = ErrorResult::class))])
    @ApiResponse(responseCode = "401", content = [Content(schema = Schema(implementation = ErrorResult::class))])
    @ApiResponse(responseCode = "500", content = [Content(schema = Schema(implementation = ErrorResult::class))])
    fun createPost(
        @GetUser user: User,
        @RequestBody @Validated request: CreatePostRequestDto,
        bindingResult: BindingResult,
    ): ResponseEntity<ResponseDto<CreatePostResultDto>> {
        val (title, content) = request
        val result = postService.createPost(title, content, user)
        return ResponseEntity.status(201).body(result)
    }

    @GetMapping("/{postId}")
    @Operation(summary = "게시글 조회 API")
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "400", content = [Content(schema = Schema(implementation = ErrorResult::class))])
    @ApiResponse(responseCode = "404", content = [Content(schema = Schema(implementation = ErrorResult::class))])
    @ApiResponse(responseCode = "500", content = [Content(schema = Schema(implementation = ErrorResult::class))])
    fun getPost(
        @PathVariable @Length(min = 36, max = 36, message = "UUID는 36자만 가능합니다.") postId: String
    ): ResponseEntity<ResponseDto<GetPostResultDto>> {
        val result = postService.getPost(UUID.fromString(postId))
        return ResponseEntity.status(200).body(result)
    }

    @PatchMapping("/{postId}")
    @Operation(summary = "게시글 수정 API")
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "400", content = [Content(schema = Schema(implementation = ErrorResult::class))])
    @ApiResponse(responseCode = "401", content = [Content(schema = Schema(implementation = ErrorResult::class))])
    @ApiResponse(responseCode = "404", content = [Content(schema = Schema(implementation = ErrorResult::class))])
    @ApiResponse(responseCode = "500", content = [Content(schema = Schema(implementation = ErrorResult::class))])
    fun updatePost(
        @GetUser user: User,
        @PathVariable @Length(min = 36, max = 36, message = "UUID는 36자만 가능합니다.") postId: String,
        @RequestBody @Validated request: UpdatePostRequestDto,
        bindingResult: BindingResult
    ): ResponseEntity<ResponseDto<UpdatePostResultDto>> {
        val result = postService.updatePost(UUID.fromString(postId), user.id!!, request)
        return ResponseEntity.status(200).body(result)
    }

    @DeleteMapping("/{postId}")
    @Operation(summary = "게시글 삭제 API")
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "400", content = [Content(schema = Schema(implementation = ErrorResult::class))])
    @ApiResponse(responseCode = "401", content = [Content(schema = Schema(implementation = ErrorResult::class))])
    @ApiResponse(responseCode = "404", content = [Content(schema = Schema(implementation = ErrorResult::class))])
    @ApiResponse(responseCode = "500", content = [Content(schema = Schema(implementation = ErrorResult::class))])
    fun deletePost(
        @GetUser user: User,
        @PathVariable @Length(min = 36, max = 36, message = "UUID는 36자만 가능합니다.") postId: String,
    ): ResponseEntity<ResponseDto<DeletePostResultDto>> {
        val result = postService.deletePost(UUID.fromString(postId), user.id!!)
        return ResponseEntity.status(200).body(result)
    }
}