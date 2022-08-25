package beyond.crud_sql.controller

import beyond.crud_sql.common.provider.JwtTokenProvider
import beyond.crud_sql.domain.Post
import beyond.crud_sql.domain.User
import beyond.crud_sql.dto.request.CreatePostRequestDto
import beyond.crud_sql.dto.request.UpdatePostRequestDto
import beyond.crud_sql.repository.PostRepository
import beyond.crud_sql.service.PostService
import beyond.crud_sql.service.UserService
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional
import java.util.*
import javax.persistence.EntityManager

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PostControllerTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val em: EntityManager,
    private val userService: UserService,
    private val postService: PostService,
    private val postRepository: PostRepository,
    private val jwtTokenProvider: JwtTokenProvider,
) {

    lateinit var user1: User
    lateinit var post1: Post
    lateinit var token: String

    @BeforeEach
    fun setUp() {
        user1 = User("mouse1@naver.com", "1234", "실험쥐1")
        em.persist(user1)

        post1 = Post("실험쥐1의 첫 게시글", "실험쥐1은 영민합니다.", user1)
        em.persist(post1)

        em.flush()

        token = userService.getUserAndToken(user1.email, user1.password).results.accessToken
    }

    @Test
    fun createPost_201() {
        // given
        val request = CreatePostRequestDto("싦험쥐1의 두번째 게시글입니다.", "실험쥐1은 재빠릅니다.")
        val json = jacksonObjectMapper().writeValueAsString(request)

        // when + then
        mockMvc.perform(
            post("/posts")
                .header("Authorization", "${jwtTokenProvider.prefix} $token")
                .content(json)
                .contentType("application/json")
                .accept("application/json")
        ).andExpect(
            status().isCreated
        ).andExpect(
            content().contentType("application/json")
        ).andExpect(
            jsonPath("\$.results.userId").value(user1.id.toString())
        ).andExpect(
            jsonPath("\$.statusCode").value(201)
        ).andExpect(
            jsonPath("\$.message").value("게시글 작성이 완료되었습니다.")
        ).andDo(print())
    }

    @Test
    fun createPost_400() {
        // given
        val request = CreatePostRequestDto("망", "")
        val json = jacksonObjectMapper().writeValueAsString(request)

        // when + then
        mockMvc.perform(
            post("/posts")
                .header("Authorization", "${jwtTokenProvider.prefix} $token")
                .content(json)
                .contentType("application/json")
                .accept("application/json")
        ).andExpect(
            status().isBadRequest
        ).andExpect(
            content().contentType("application/json")
        ).andExpect(
            jsonPath("\$.error").value("Invalid Request")
        ).andExpect(
            jsonPath("\$.statusCode").value(400)
        ).andExpect(
            jsonPath("\$.message").value("유효성 검사 에러입니다.")
        ).andExpect(
            jsonPath("\$.validation.content.size()").value(2)
        ).andDo(print())
    }

    @Test
    fun createPost_401() {
        // given
        val request = CreatePostRequestDto("싦험쥐1의 두번째 게시글입니다.", "실험쥐1은 재빠릅니다.")
        val json = jacksonObjectMapper().writeValueAsString(request)

        // when + then
        mockMvc.perform(
            post("/posts")
                .header("Authorization", "${jwtTokenProvider.prefix} 1s23")
                .content(json)
                .contentType("application/json")
                .accept("application/json")
        ).andExpect(
            status().isUnauthorized
        ).andExpect(
            content().contentType("application/json")
        ).andExpect(
            jsonPath("\$.error").value("Unauthorized")
        ).andExpect(
            jsonPath("\$.statusCode").value(401)
        ).andExpect(
            jsonPath("\$.message").value("토큰 검증에 실패하였습니다.")
        ).andDo(print())
    }

    @Test
    fun getPost_200() {
        // when + then
        mockMvc.perform(
            get("/posts/${post1.id}")
        ).andExpect(
            status().isOk
        ).andExpect(
            content().contentType("application/json")
        ).andExpect(
            jsonPath("\$.results.postId").value(post1.id.toString())
        ).andExpect(
            jsonPath("\$.results.userId").value(user1.id.toString())
        ).andExpect(
            jsonPath("\$.results.writer").value(user1.nickname)
        ).andExpect(
            jsonPath("\$.results.title").value(post1.title)
        ).andExpect(
            jsonPath("\$.results.content").value(post1.content)
        ).andExpect(
            jsonPath("\$.results.createdDate").value(post1.createdDate.toString())
        ).andExpect(
            jsonPath("\$.results.lastModifiedDate").value(post1.lastModifiedDate.toString())
        ).andExpect(
            jsonPath("\$.statusCode").value(200)
        ).andExpect(
            jsonPath("\$.message").value("게시글 조회가 완료되었습니다.")
        ).andDo(print())
    }

    @Test
    fun getPost_400() {
        // when + then
        mockMvc.perform(
            get("/posts/${post1.id}add")
        ).andExpect(
            status().isBadRequest
        ).andExpect(
            content().contentType("application/json")
        ).andExpect(
            jsonPath("\$.error").value("Invalid Request")
        ).andExpect(
            jsonPath("\$.statusCode").value(400)
        ).andExpect(
            jsonPath("\$.message").value("유효성 검사 에러입니다.")
        ).andExpect(
            jsonPath("\$.validation.postId").value(mutableListOf("UUID는 36자만 가능합니다."))
        ).andDo(print())
    }

    @Test
    fun getPost_404() {
        // when + then
        mockMvc.perform(
            get("/posts/${UUID.randomUUID()}")
        ).andExpect(
            status().isNotFound
        ).andExpect(
            content().contentType("application/json")
        ).andExpect(
            jsonPath("\$.error").value("Not Found")
        ).andExpect(
            jsonPath("\$.statusCode").value(404)
        ).andExpect(
            jsonPath("\$.message").value("게시글을 찾을 수 없습니다.")
        ).andDo(print())
    }

    @Test
    fun updatePost_200() {
        // given
        val request = UpdatePostRequestDto(null, "실험쥐1은 똘똘합니다.")
        val json = jacksonObjectMapper().writeValueAsString(request)

        // when + then
        mockMvc.perform(
            patch("/posts/${post1.id}")
                .header("Authorization", "${jwtTokenProvider.prefix} $token")
                .content(json)
                .contentType("application/json")
                .accept("application/json")
        ).andExpect(
            status().isOk
        ).andExpect(
            content().contentType("application/json")
        ).andExpect(
            jsonPath("\$.results.postId").value(post1.id.toString())
        ).andExpect(
            jsonPath("\$.results.userId").value(user1.id.toString())
        ).andExpect(
            jsonPath("\$.statusCode").value(200)
        ).andExpect(
            jsonPath("\$.message").value("게시글 수정이 완료되었습니다.")
        ).andDo(print())
    }

    @Test
    fun updatePost_400() {
        // given
        val request = UpdatePostRequestDto(null, "")
        val json = jacksonObjectMapper().writeValueAsString(request)

        // when + then
        mockMvc.perform(
            patch("/posts/${post1.id}")
                .header("Authorization", "${jwtTokenProvider.prefix} $token")
                .content(json)
                .contentType("application/json")
                .accept("application/json")
        ).andExpect(
            status().isBadRequest
        ).andExpect(
            content().contentType("application/json")
        ).andExpect(
            jsonPath("\$.error").value("Invalid Request")
        ).andExpect(
            jsonPath("\$.statusCode").value(400)
        ).andExpect(
            jsonPath("\$.message").value("유효성 검사 에러입니다.")
        ).andExpect(
            jsonPath("\$.validation.content").value(mutableListOf("1자 이상이어야 합니다."))
        ).andDo(print())
    }

    @Test
    fun updatePost_404() {
        // given
        val request = UpdatePostRequestDto(null, "실험쥐1은 똘똘합니다.")
        val json = jacksonObjectMapper().writeValueAsString(request)

        // when + then
        mockMvc.perform(
            patch("/posts/${UUID.randomUUID()}")
                .header("Authorization", "${jwtTokenProvider.prefix} $token")
                .content(json)
                .contentType("application/json")
                .accept("application/json")
        ).andExpect(
            status().isNotFound
        ).andExpect(
            content().contentType("application/json")
        ).andExpect(
            jsonPath("\$.error").value("Not Found")
        ).andExpect(
            jsonPath("\$.statusCode").value(404)
        ).andExpect(
            jsonPath("\$.message").value("게시글을 찾을 수 없습니다.")
        ).andDo(print())
    }

    @Test
    fun deletePost_200() {
        // given + when + then
        mockMvc.perform(
            delete("/posts/${post1.id}")
                .header("Authorization", "${jwtTokenProvider.prefix} $token")
        ).andExpect(
            status().isOk
        ).andExpect(
            content().contentType("application/json")
        ).andExpect(
            jsonPath("\$.results.postId").value(post1.id.toString())
        ).andExpect(
            jsonPath("\$.results.userId").value(user1.id.toString())
        ).andExpect(
            jsonPath("\$.statusCode").value(200)
        ).andExpect(
            jsonPath("\$.message").value("게시글 삭제가 완료되었습니다.")
        ).andDo(print())
    }

    @Test
    fun getPostAll_200() {
        // given
        postRepository.deleteById(post1.id!!)
        val pageRequest = PageRequest.of(0, 3)
        val result = postService.getPostAll(pageRequest)
        val getPosts = result?.results

        // when + then
        mockMvc.get("/posts?page=0&size=3")
            .andExpect {
                status().isOk
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json(
                        // language=json
                        """
                        {
                            "results": {
                                "posts": [
                                    {
                                        "postId": "${getPosts!!.posts[0].postId}",
                                        "userId": "${getPosts.posts[0].userId}",
                                        "writer": "${getPosts.posts[0].writer}",
                                        "title": "${getPosts.posts[0].title}",
                                        "content": "${getPosts.posts[0].content}",
                                        "createdDate": "${getPosts.posts[0].createdDate}",
                                        "lastModifiedDate": "${getPosts.posts[0].lastModifiedDate}"
                                    },
                                    {
                                        "postId": "${getPosts.posts[1].postId}",
                                        "userId": "${getPosts.posts[1].userId}",
                                        "writer": "${getPosts.posts[1].writer}",
                                        "title": "${getPosts.posts[1].title}",
                                        "content": "${getPosts.posts[1].content}",
                                        "createdDate": "${getPosts.posts[1].createdDate}",
                                        "lastModifiedDate": "${getPosts.posts[1].lastModifiedDate}"
                                    },
                                    {
                                        "postId": "${getPosts.posts[2].postId}",
                                        "userId": "${getPosts.posts[2].userId}",
                                        "writer": "${getPosts.posts[2].writer}",
                                        "title": "${getPosts.posts[2].title}",
                                        "content": "${getPosts.posts[2].content}",
                                        "createdDate": "${getPosts.posts[2].createdDate}",
                                        "lastModifiedDate": "${getPosts.posts[2].lastModifiedDate}"
                                    }
                                ],
                                "totalPages": ${getPosts.totalPages},
                                "totalElements": ${getPosts.totalElements},
                                "numberOfElements": ${getPosts.numberOfElements},
                                "pageNumber": ${getPosts.pageNumber},
                                "pageSize": ${getPosts.pageSize},
                                "isFirst": ${getPosts.isFirst},
                                "isNext": ${getPosts.isNext}
                            },
                            "statusCode": 200,
                            "message": "게시글 리스트 조회가 완료되었습니다."
                        }                       
                        """.trimIndent()
                    )
                }
            }

    }


    @Test
    fun getPostAll_400() {
        // when + then
        mockMvc.get("/posts?page=-1&size=0")
            .andExpect {
                status().isBadRequest
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json(
                        // language=json
                        """
                        {
                            "error": "Invalid Request",
                            "statusCode": 400,
                            "message": "유효성 검사 에러입니다.",
                            "validation": {
                                "page": [
                                    "0 이상이어야 합니다."
                                ],
                                "size": [
                                    "1 이상이어야 합니다."
                                ]
                            },
                            "cause": null
                        }
                        """.trimIndent()
                    )
                }
            }.andDo { print() }

    }

}