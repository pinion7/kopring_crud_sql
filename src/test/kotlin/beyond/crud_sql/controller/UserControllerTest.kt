package beyond.crud_sql.controller

import beyond.crud_sql.common.provider.JwtTokenProvider
import beyond.crud_sql.domain.User
import beyond.crud_sql.dto.request.CreateUserRequestDto
import beyond.crud_sql.dto.request.UpdateUserRequestDto
import beyond.crud_sql.service.UserService
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional
import java.util.*
import javax.persistence.EntityManager


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val em: EntityManager,
    private val userService: UserService,
    private val jwtTokenProvider: JwtTokenProvider
) {

    lateinit var user1: User
    lateinit var token: String

    @BeforeEach
    fun setUp() {
        user1 = User("mouse1@naver.com", "1234", "실험쥐1")
        em.persist(user1)
        em.flush()

        token = userService.getUserAndToken(user1.email, user1.password).results.accessToken
    }

    @Test
    fun createUser_success() {
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
    fun createUser_fail_400() {
        // given
        val request = CreateUserRequestDto().apply {
            email = "email.com"
            password = "long.long.long.long.long.long.long"
        }
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
            jsonPath("\$.validation.nickname").value(mutableListOf("필드 값이 유효하지 않습니다."))
        ).andDo(print())
    }

    @Test
    fun createUser_fail_409() {
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
    fun getUser_success() {
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
    fun getUser_fail_400() {
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
    fun getUser_fail_404() {
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
    fun updateUser_success() {
        // given
        val request = UpdateUserRequestDto(user1.id.toString(), "새로운 닉네임")
        val json = jacksonObjectMapper().writeValueAsString(request)

        // when + then
        mockMvc.perform(
            patch("/users/${user1.id}")
                .header("Authorization", "${jwtTokenProvider.prefix} $token" )
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
    fun updateUser_fail_400() {
        // given
        val request = UpdateUserRequestDto(user1.id.toString() + "d", "long.long.long.long")
        val json = jacksonObjectMapper().writeValueAsString(request)

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
            jsonPath("\$.validation.userId").value(mutableListOf("UUID는 36자만 가능합니다."))
        ).andExpect(
            jsonPath("\$.validation.nickname").value(mutableListOf("2자 이상 10자 이하여야 합니다."))
        ).andDo(print())
    }

    @Test
    fun updateUser_fail_404() {
        // given
        val request = UpdateUserRequestDto(UUID.randomUUID().toString(), "새로운 닉네임")
        val json = jacksonObjectMapper().writeValueAsString(request)

        // when + then
        mockMvc.perform(
            patch("/users/${user1.id}")
                .header("Authorization", "${jwtTokenProvider.prefix} $token" )
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
    fun updateUser_fail_409() {
        // given

        // when + then
    }

    @Test
    fun deleteUser_success() {
        // given + when + then
        mockMvc.perform(
            delete("/users/${user1.id}")
                .header("Authorization", "${jwtTokenProvider.prefix} $token" )
        ).andExpect(
            status().isOk
        ).andExpect(
            content().contentType("application/json")
        ).andExpect(
            jsonPath("\$.results.userId").value(user1.id.toString())
        ).andExpect(
            jsonPath("\$.statusCode").value(200)
        ).andExpect(
            jsonPath("\$.message").value("회원 탈퇴가 완료되었습니다.")
        ).andDo(print())
    }


    @Test
    fun deleteUser_fail_400() {
        // given + when + then
        mockMvc.perform(
            delete("/users/${user1.id}d")
                .header("Authorization", "${jwtTokenProvider.prefix} $token" )
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

}