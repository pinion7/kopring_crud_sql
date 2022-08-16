package beyond.crud_sql.controller

import beyond.crud_sql.domain.User
import beyond.crud_sql.dto.CreateUserDto
import beyond.crud_sql.repository.UserRepository
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
class UserControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var em: EntityManager;

    @Autowired
    lateinit var userRepository: UserRepository

    @BeforeEach
    fun setUp() {
        val user = User("mouse1@naver.com", "1234", "실험쥐1")
        em.persist(user)
    }

    @Test
    fun createUserTest_success() {
        // given
        val request = CreateUserDto("mouse2@naver.com", "1234", "실험쥐2")
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
        val result = userRepository.findAll()
        println(result[0])
        // given
        val request = CreateUserDto("mouse1@naver.com", "1234", "실험쥐1")
        val json = jacksonObjectMapper().writeValueAsString(request)

        // when + then
        mockMvc.perform(
            post("/users")
                .content(json)
                .contentType("application/json")
                .accept("application/json")
        ).andExpect(
            status().isInternalServerError
        ).andExpect(
            jsonPath("\$.error").value("Internal Server Error")
        ).andExpect(
            jsonPath("\$.statusCode").value(500)
        ).andExpect(
            jsonPath("\$.message").value("이메일 혹은 닉네임 중복입니다.")
        ).andDo(print())
    }


}