package beyond.crud_sql.controller

import beyond.crud_sql.common.provider.JwtTokenProvider
import beyond.crud_sql.domain.User
import beyond.crud_sql.dto.request.CreateUserRequestDto
import beyond.crud_sql.dto.request.UpdateUserRequestDto
import beyond.crud_sql.repository.PostRepository
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
import java.text.SimpleDateFormat
import java.util.*
import javax.persistence.EntityManager


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val em: EntityManager,
    private val userService: UserService,
    private val postRepository: PostRepository,
    private val jwtTokenProvider: JwtTokenProvider,
) {

    private val transFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    lateinit var user1: User
    lateinit var token: String
    lateinit var pageRequest: PageRequest

    @BeforeEach
    fun setUp() {
        user1 = User("mouse1@naver.com", "1234", "실험쥐1")
        em.persist(user1)
        em.flush()

        token = userService.getUserAndToken(user1.email, user1.password).results.accessToken
        pageRequest = PageRequest.of(
            0, 10, Sort.by(Sort.Direction.DESC, "createdDate")
        )
    }

    @Test
    fun createUser_200() {
        // given
        val request = CreateUserRequestDto("mouse2@naver.com", "1234", "실험쥐2")
        val json = jacksonObjectMapper().writeValueAsString(request)

        // when + then
        mockMvc.perform(
            post("/users")
                .content(json)
                .contentType("application/json")
                .accept("application/json")
        ).andExpect(
            status().isCreated
        ).andExpect(
            content().contentType("application/json")
        ).andExpect(
            jsonPath("\$.statusCode").value(201)
        ).andExpect(
            jsonPath("\$.message").value("회원 등록에 성공하였습니다.")
        ).andDo(print())
    }

    @Test
    fun createUser_400() {
        // given
        val request = CreateUserRequestDto("email.com", "long.long.long.long.long.long.long", "")
        val json = jacksonObjectMapper().writeValueAsString(request)

        // when + then
        mockMvc.perform(
            post("/users")
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
            jsonPath("\$.validation.email").value(mutableListOf("올바른 email 형식이 아닙니다."))
        ).andExpect(
            jsonPath("\$.validation.password").value(mutableListOf("4자 이상 12자 이하여야 합니다."))
        ).andExpect(
            jsonPath("\$.validation.nickname.size()").value(2)
        ).andDo(print())
    }

    @Test
    fun createUser_409() {
        // given
        val request = CreateUserRequestDto(user1.email, user1.password, user1.nickname)
        val json = jacksonObjectMapper().writeValueAsString(request)

//        // when + then
//        mockMvc.perform(
//            post("/users")
//                .content(json)
//                .contentType("application/json")
//                .accept("application/json")
//        ).andExpect(
//            status().isConflict
//        ).andExpect(
//            jsonPath("\$.error").value("Conflict")
//        ).andExpect(
//            jsonPath("\$.statusCode").value(409)
//        ).andExpect(
//            jsonPath("\$.message").value("이메일 혹은 닉네임 중복입니다.")
//        ).andExpect(
//            jsonPath("\$.cause").value("ERROR: duplicate key value violates unique constraint \"uk_6dotkott2kjsp8vw4d0m25fb7\"\n  Detail: Key (email)=(mouse1@naver.com) already exists.")
//        ).andDo(print())
    }

    @Test
    fun getUser_200() {
        // given + when + then
        mockMvc.perform(
            get("/users/${user1.id}")
        ).andExpect(
            status().isOk
        ).andExpect(
            content().contentType("application/json")
        ).andExpect(
            jsonPath("\$.results.userId").value(user1.id.toString())
        ).andExpect(
            jsonPath("\$.results.email").value(user1.email)
        ).andExpect(
            jsonPath("\$.results.nickname").value(user1.nickname)
        ).andExpect(
            jsonPath("\$.results.quit").value(user1.quit)
        ).andExpect(
            jsonPath("\$.results.createdDate").value(user1.createdDate.toString())
        ).andExpect(
            jsonPath("\$.results.lastModifiedDate").value(user1.lastModifiedDate.toString())
        ).andExpect(
            jsonPath("\$.statusCode").value(200)
        ).andExpect(
            jsonPath("\$.message").value("회원 정보 조회가 완료되었습니다.")
        ).andDo(print())
    }

    @Test
    fun getUser_400() {
        // given
        val userId = "b38d098e-b757-4fff-9ffd-3580e415ede61342452353145316126"

        // when + then
        mockMvc.perform(
            get("/users/$userId")
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
            jsonPath("\$.validation.userId").value(mutableListOf("UUID는 36자만 가능합니다."))
        ).andDo(print())
    }

    @Test
    fun getUser_404() {
        // given
        val userId = UUID.randomUUID()

        // when + then
        mockMvc.perform(
            get("/users/$userId")
        ).andExpect(
            status().isNotFound
        ).andExpect(
            content().contentType("application/json")
        ).andExpect(
            jsonPath("\$.error").value("Not Found")
        ).andExpect(
            jsonPath("\$.statusCode").value(404)
        ).andExpect(
            jsonPath("\$.message").value("존재하지 않는 유저 입니다.")
        ).andDo(print())
    }

    @Test
    fun updateUser_200() {
        // given
        val request = UpdateUserRequestDto(user1.id.toString(), "새로운 닉네임")
        val json = jacksonObjectMapper().writeValueAsString(request)

        // when + then
        mockMvc.perform(
            patch("/users/${user1.id}")
                .header("Authorization", "${jwtTokenProvider.prefix} $token")
                .content(json)
                .contentType("application/json")
                .accept("application/json")
        ).andExpect(
            status().isOk
        ).andExpect(
            content().contentType("application/json")
        ).andExpect(
            jsonPath("\$.results.userId").value(user1.id.toString())
        ).andExpect(
            jsonPath("\$.statusCode").value(200)
        ).andExpect(
            jsonPath("\$.message").value("회원 정보 수정이 완료되었습니다.")
        ).andDo(print())
    }

    @Test
    fun updateUser_400() {
        // given
        val request = UpdateUserRequestDto(user1.id.toString(), "long.long.long.long")
        val json = jacksonObjectMapper().writeValueAsString(request)
        println(request)

        // when + then
        mockMvc.perform(
            patch("/users/${user1.id}")
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
            jsonPath("\$.validation.nickname").value(mutableListOf("2자 이상 10자 이하여야 합니다."))
        ).andDo(print())
    }

    @Test
    fun updateUser_404() {
        // given
        val request = UpdateUserRequestDto(user1.id.toString(), "새로운 닉네임")
        val json = jacksonObjectMapper().writeValueAsString(request)

        // when
        userService.deleteUser(user1)

        // then
        mockMvc.perform(
            patch("/users/${user1.id}")
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
            jsonPath("\$.message").value("존재하지 않는 유저 입니다.")
        ).andDo(print())
    }

    @Test
    fun updateUser_409() {
        // given

        // when + then
    }

    @Test
    fun deleteUser_200() {
        // given + when + then
        mockMvc.perform(
            delete("/users/${user1.id}")
                .header("Authorization", "${jwtTokenProvider.prefix} $token")
        ).andExpect(
            status().isOk
        ).andExpect(
            content().contentType("application/json")
        ).andExpect(
            jsonPath("\$.results.userId").value(user1.id.toString())
        ).andExpect(
            jsonPath("\$.results.quit").value(true)
        ).andExpect(
            jsonPath("\$.statusCode").value(200)
        ).andExpect(
            jsonPath("\$.message").value("회원 탈퇴가 완료되었습니다.")
        ).andDo(print())
    }

    @Test
    fun deleteUser_400() {
        // given + when + then
        mockMvc.perform(
            delete("/users/${user1.id}d")
                .header("Authorization", "${jwtTokenProvider.prefix} $token")
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
            jsonPath("\$.validation.userId").value("UUID는 36자만 가능합니다.")
        ).andDo(print())
    }

    @Test
    fun getUserWithPostAll_200() {
        // given
        val bdd = userService.getUserAndToken("bdd@ns.com", "Bdd").results
        val result = postRepository.findPostAllByUserId(bdd.userId, pageRequest)
        val posts = result.content

        // when + then
        mockMvc.get("/users/${bdd.userId}/posts")
            .andExpect {
                status { isOk() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json(
                        // language=json
                        """
                        {
                            "results": {
                                "userId": "${bdd.userId}",
                                "posts": [
                                    {
                                        "postId": "${posts[0].id}",
                                        "userId": "${bdd.userId}",
                                        "writer": "${bdd.nickname}",
                                        "title": "${posts[0].title}",
                                        "content": "${posts[0].content}",
                                        "createdDate": "${posts[0].createdDate}",
                                        "lastModifiedDate": "${posts[0].lastModifiedDate}"
                                    },
                                    {
                                        "postId": "${posts[1].id}",
                                        "userId": "${bdd.userId}",
                                        "writer": "${bdd.nickname}",
                                        "title": "${posts[1].title}",
                                        "content": "${posts[1].content}",
                                        "createdDate": "${posts[1].createdDate}",
                                        "lastModifiedDate": "${posts[1].lastModifiedDate}"
                                    },
                                    {
                                        "postId": "${posts[2].id}",
                                        "userId": "${bdd.userId}",
                                        "writer": "${bdd.nickname}",
                                        "title": "${posts[2].title}",
                                        "content": "${posts[2].content}",
                                        "createdDate": "${posts[2].createdDate}",
                                        "lastModifiedDate": "${posts[2].lastModifiedDate}"
                                    }
                                ],
                                "totalPages": ${result.totalPages},
                                "totalElements": ${result.totalElements},
                                "numberOfElements": ${result.numberOfElements},
                                "pageNumber": ${result.number},
                                "pageSize": ${result.size},
                                "isFirst": ${result.isFirst},
                                "isNext": ${result.hasNext()}
                            },
                            "statusCode": 200,
                            "message": "회원 게시글 조회가 완료되었습니다."
                        }
                        """.trimIndent()
                    )
                }
            }.andDo { print() }
    }

    @Test
    fun getUserWithPostAll_200_empty() {
        val uuid = UUID.randomUUID()
        val result = postRepository.findPostAllByUserId(uuid, pageRequest)

        // when + then
        mockMvc.get("/users/${uuid}/posts")
            .andExpect {
                status { isOk() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json(
                        // language=json
                        """
                        {
                            "results": {
                                "userId": "$uuid",
                                "posts": [],
                                "totalPages": ${result.totalPages},
                                "totalElements": ${result.totalElements},
                                "numberOfElements": ${result.numberOfElements},
                                "pageNumber": ${result.number},
                                "pageSize": ${result.size},
                                "isFirst": ${result.isFirst},
                                "isNext": ${result.hasNext()}
                            },
                            "statusCode": 200,
                            "message": "회원 게시글 조회가 완료되었습니다."
                        }
                        """.trimIndent()
                    )
                }
            }.andDo { print() }
    }

    @Test
    fun getUserWithPostAll_400() {
        val result = postRepository.findPostAllByUserId(user1.id!!, pageRequest)

        // when + then
        mockMvc.get("/users/${user1.id}/posts?page=-1&size=0")
            .andExpect {
                status { isBadRequest() }
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
                                "size": [
                                    "1 이상이어야 합니다."
                                ],
                                "page": [
                                    "0 이상이어야 합니다."
                                ]
                            },
                            "cause": null
                        }
                        """.trimIndent()
                    )
                }
            }.andDo { print() }
    }

    @Test
    fun searchUserAll_200() {
        // given
        val bdd = userService.getUserAndToken("bdd@ns.com", "Bdd").results
        val bddResult = postRepository.findPostAllByUserId(bdd.userId, pageRequest)
        val bddPosts = bddResult.content

        val chovy = userService.getUserAndToken("chovy@geng.com", "Chovy").results
        val chovyResult = postRepository.findPostAllByUserId(chovy.userId, pageRequest)
        val chovyPosts = chovyResult.content

        // when + then
        mockMvc.get("/users/search?email=.com&nickname=비&page=0&size=2")
            .andExpect {
                status { isOk() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json(
                        // language=json
                        """
                        {
                            "results": {
                                "users": [
                                    {
                                        "userId": "${bdd.userId}",
                                        "email": "${bdd.email}",
                                        "nickname": "${bdd.nickname}",
                                        "quit": false,
                                        "createdDate": "${bdd.createdDate}",
                                        "lastModifiedDate": "${bdd.lastModifiedDate}",
                                        "posts": [
                                            {
                                                "postId": "${bddPosts[0].id}",
                                                "title": "${bddPosts[0].title}",
                                                "content": "${bddPosts[0].content}",
                                                "createdDate": "${bddPosts[0].createdDate}",
                                                "lastModifiedDate": "${bddPosts[0].lastModifiedDate}"
                                            },
                                            {
                                                "postId": "${bddPosts[1].id}",
                                                "title": "${bddPosts[1].title}",
                                                "content": "${bddPosts[1].content}",
                                                "createdDate": "${bddPosts[1].createdDate}",
                                                "lastModifiedDate": "${bddPosts[1].lastModifiedDate}"
                                            },
                                            {
                                                "postId": "${bddPosts[2].id}",
                                                "title": "${bddPosts[2].title}",
                                                "content": "${bddPosts[2].content}",
                                                "createdDate": "${bddPosts[2].createdDate}",
                                                "lastModifiedDate": "${bddPosts[2].lastModifiedDate}"
                                            }
                                        ]
                                    },
                                    {
                                        "userId": "${chovy.userId}",
                                        "email": "${chovy.email}",
                                        "nickname": "${chovy.nickname}",
                                        "quit": false,
                                        "createdDate": "${chovy.createdDate}",
                                        "lastModifiedDate": "${chovy.lastModifiedDate}",
                                        "posts": [
                                            {
                                                "postId": "${chovyPosts[0].id}",
                                                "title": "${chovyPosts[0].title}",
                                                "content": "${chovyPosts[0].content}",
                                                "createdDate": "${chovyPosts[0].createdDate}",
                                                "lastModifiedDate": "${chovyPosts[0].lastModifiedDate}"
                                            },
                                            {
                                                "postId": "${chovyPosts[1].id}",
                                                "title": "${chovyPosts[1].title}",
                                                "content": "${chovyPosts[1].content}",
                                                "createdDate": "${chovyPosts[1].createdDate}",
                                                "lastModifiedDate": "${chovyPosts[1].lastModifiedDate}"
                                            },
                                            {
                                                "postId": "${chovyPosts[2].id}",
                                                "title": "${chovyPosts[2].title}",
                                                "content": "${chovyPosts[2].content}",
                                                "createdDate": "${chovyPosts[2].createdDate}",
                                                "lastModifiedDate": "${chovyPosts[2].lastModifiedDate}"
                                            },
                                            {
                                                "postId": "${chovyPosts[3].id}",
                                                "title": "${chovyPosts[3].title}",
                                                "content": "${chovyPosts[3].content}",
                                                "createdDate": "${chovyPosts[3].createdDate}",
                                                "lastModifiedDate": "${chovyPosts[3].lastModifiedDate}"
                                            }
                                        ]
                                    }
                                ],
                                "totalPages": 1,
                                "totalElements": 2,
                                "numberOfElements": 2,
                                "pageNumber": 0,
                                "pageSize": 2,
                                "isFirst": true,
                                "isNext": false
                            },
                            "statusCode": 200,
                            "message": "회원 리스트 조건 검색이 완료되었습니다."
                        }
                        """.trimIndent()
                    )
                }
            }.andDo { print() }
    }

    @Test
    fun searchUserAll_200_empty() {
        // when + then
        mockMvc.get("/users/search?email=.com&nickname=없는유&page=0&size=10")
            .andExpect {
                status { isOk() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json(
                        // language=json
                        """
                        {
                            "results": {
                                "users": [],
                                "totalPages": 0,
                                "totalElements": 0,
                                "numberOfElements": 0,
                                "pageNumber": 0,
                                "pageSize": 10,
                                "isFirst": true,
                                "isNext": false
                            },
                            "statusCode": 200,
                            "message": "회원 리스트 조건 검색이 완료되었습니다."
                        }    
                        """.trimIndent()
                    )
                }
            }.andDo { print() }
    }


    @Test
    fun searchUserAll_400() {
        // when + then
        mockMvc.get("/users/search?page=-1&size=0")
            .andExpect {
                status { isBadRequest() }
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
                                "size": [
                                    "1 이상이어야 합니다."
                                ],
                                "page": [
                                    "0 이상이어야 합니다."
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