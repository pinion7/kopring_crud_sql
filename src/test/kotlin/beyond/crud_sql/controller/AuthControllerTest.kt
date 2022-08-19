package beyond.crud_sql.controller

import beyond.crud_sql.domain.User
import beyond.crud_sql.dto.request.LoginRequestDto
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
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
    fun login_fail1() {
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
            jsonPath("\$.error").value("Not Found")
        ).andExpect(
            jsonPath("\$.statusCode").value(404)
        ).andExpect(
            jsonPath("\$.message").value("존재하지 않는 이메일 입니다.")
        ).andDo(print())
    }

    @Test
    fun login_fail2() {
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
            jsonPath("\$.error").value("Not Found")
        ).andExpect(
            jsonPath("\$.statusCode").value(404)
        ).andExpect(
            jsonPath("\$.message").value("비밀번호가 일치하지 않습니다.")
        ).andDo(print())
    }
}