package beyond.crud_sql.controller

import beyond.crud_sql.domain.User
import beyond.crud_sql.dto.request.CreateUserRequestDto
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
class UserControllerTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val em: EntityManager,
){

    lateinit var user1: User

    @BeforeEach
    fun setUp() {
        user1 = User("mouse1@naver.com", "1234", "실험쥐1")
        em.persist(user1)
        em.flush()
    }

    @Test
    fun createUserTest_success() {
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
            jsonPath("\$.statusCode").value(201)
        ).andExpect(
            jsonPath("\$.message").value("유저 등록에 성공하였습니다.")
        ).andDo(print())
    }

    @Test
    fun createUserTest_fail() {
        // given
        val request = CreateUserRequestDto(user1.email, user1.password, user1.nickname)
        val json = jacksonObjectMapper().writeValueAsString(request)

        // when + then
        mockMvc.perform(
            post("/users")
                .content(json)
                .contentType("application/json")
                .accept("application/json")
        ).andExpect(
            status().isConflict
        ).andExpect(
            jsonPath("\$.error").value("Conflict")
        ).andExpect(
            jsonPath("\$.statusCode").value(409)
        ).andExpect(
            jsonPath("\$.message").value("이메일 혹은 닉네임 중복입니다.")
        ).andExpect(
            jsonPath("\$.cause").value("ERROR: duplicate key value violates unique constraint \"uk_6dotkott2kjsp8vw4d0m25fb7\"\n  Detail: Key (email)=(mouse1@naver.com) already exists.")
        ).andDo(print())
    }
}