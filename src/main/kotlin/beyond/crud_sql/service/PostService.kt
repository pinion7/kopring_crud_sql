package beyond.crud_sql.service

import beyond.crud_sql.common.exception.custom.NotFoundException
import beyond.crud_sql.domain.Post
import beyond.crud_sql.domain.User
import beyond.crud_sql.dto.condition.PostSearchCondition
import beyond.crud_sql.dto.request.UpdatePostRequestDto
import beyond.crud_sql.dto.response.ResponseDto
import beyond.crud_sql.dto.result.*
import beyond.crud_sql.repository.PostRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class PostService(
    private val postRepository: PostRepository,
) {

    @Transactional
    fun createPost(title: String, content: String, user: User): ResponseDto<CreatePostResultDto> {
        val savedPost = postRepository.save(Post(title, content, user))
        return ResponseDto(
            CreatePostResultDto(savedPost.id!!, user.id!!),
            201,
            "게시글 작성이 완료되었습니다."
        )
    }

    fun getPost(postId: UUID): ResponseDto<GetPostResultDto> {
        val findPost = findPostByPostId(postId)
        return ResponseDto(
            GetPostResultDto(
                findPost.id!!,
                findPost.user.id!!,
                findPost.user.nickname,
                findPost.title,
                findPost.content,
                findPost.createdDate!!,
                findPost.lastModifiedDate!!,
            ),
            200,
            "게시글 조회가 완료되었습니다."
        )
    }

    @Transactional
    fun updatePost(postId: UUID, userId: UUID, params: UpdatePostRequestDto): ResponseDto<UpdatePostResultDto> {
        val post = findPostByPostIdAndUserId(postId, userId)
        post.updatePostFields(params)
        return ResponseDto(
            UpdatePostResultDto(post.id!!, post.user.id!!),
            200,
            "게시글 수정이 완료되었습니다."
        )
    }

    @Transactional
    fun deletePost(postId: UUID, userId: UUID): ResponseDto<DeletePostResultDto> {
        postRepository.deleteByIdAndUserId(postId, userId)
        return ResponseDto(
            DeletePostResultDto(postId, userId),
            200,
            "게시글 삭제가 완료되었습니다."
        )
    }

    fun getUserWithPostAll(userId: UUID, pageRequest: PageRequest): ResponseDto<GetUserWithPostAllResultDto> {
        val pageablePostList = postRepository.findPostAllByUserId(userId, pageRequest)

        return ResponseDto(
            GetUserWithPostAllResultDto(
                userId,
                pageablePostList
            ),
            200,
            "회원 게시글 조회가 완료되었습니다."
        )

    }

    fun getPostAll(pageRequest: PageRequest): ResponseDto<GetPostAllResultDto> {
        val pageablePostList = postRepository.findPostAll(pageRequest)

        return ResponseDto(
            GetPostAllResultDto(
                pageablePostList
            ),
            200,
            "게시글 리스트 조회가 완료되었습니다."
        )
    }

    fun searchPostAll(
        condition: PostSearchCondition,
        pageable: Pageable,
    ): ResponseDto<SearchPostAllResultDto> {
        val postList = postRepository.findPostAllWithCondition(condition, pageable)

        return ResponseDto(
            SearchPostAllResultDto(postList),
            200,
            "게시글 리스트 조건 검색이 완료되었습니다."
        )
    }

    private fun findPostByPostId(postId: UUID): Post {
        return postRepository.findByIdOrNull(postId) ?: throw NotFoundException("게시글을 찾을 수 없습니다.")
    }


    private fun findPostByPostIdAndUserId(postId: UUID, userId: UUID): Post {
        val findPost = postRepository.findByIdAndUserId(postId, userId)
        if (findPost.isEmpty()) {
            throw NotFoundException("게시글을 찾을 수 없습니다.")
        }
        return findPost[0]
    }
}
