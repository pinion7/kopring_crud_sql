package beyond.crud_sql.controller

import beyond.crud_sql.domain.User
import beyond.crud_sql.dto.request.CreateUserRequestDto
import beyond.crud_sql.dto.request.LoginRequestDto
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val em: EntityManager,
) {
    lateinit var user: User

    @BeforeEach
    fun setUp() {
        user = User("test@naver.com", "1234", "테스트유저")
        em.persist(user)
        em.flush()
    }

    @Test
    fun login_success() {
        // given
        val request = LoginRequestDto("test@naver.com", "1234")
        val json = jacksonObjectMapper().writeValueAsString(request)

        // when + then
        mockMvc.perform(
            post("/auth/login")
                .content(json)
                .contentType("application/json")
                .accept("application/json")
        ).andExpect(
            status().isOk
        ).andExpect(
            content().contentType("application/json")
        ).andExpect(
            jsonPath("\$.results.email").value(user.email)
        ).andExpect(
            jsonPath("\$.results.nickname").value(user.nickname)
        ).andExpect(
            jsonPath("\$.statusCode").value(200)
        ).andExpect(
            jsonPath("\$.message").value("로그인에 성공하였습니다.")
        ).andDo(print())
    }

    @Test
    fun login_fail_400() {
        // given
        val request = LoginRequestDto().apply {
            email = "email.com"
            password = "long.long.long.long.long.long.long"
        }
        val json = jacksonObjectMapper().writeValueAsString(request)

        // when + then
        mockMvc.perform(
            post("/auth/login")
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
        ).andDo(print())
    }

    @Test
    fun login_fail_404_case1() {
        // given
        val request = LoginRequestDto("wrong@naver.com", "1234")
        val json = jacksonObjectMapper().writeValueAsString(request)

        // when + then
        mockMvc.perform(
            post("/auth/login")
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
            jsonPath("\$.message").value("존재하지 않는 이메일 입니다.")
        ).andDo(print())
    }

    @Test
    fun login_fail_404_case2() {
        // given
        val request = LoginRequestDto("test@naver.com", "12345")
        val json = jacksonObjectMapper().writeValueAsString(request)

        // when + then
        mockMvc.perform(
            post("/auth/login")
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
            jsonPath("\$.message").value("비밀번호가 일치하지 않습니다.")
        ).andDo(print())
    }
}