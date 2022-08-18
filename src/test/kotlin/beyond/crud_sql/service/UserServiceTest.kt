package beyond.crud_sql.service

import beyond.crud_sql.common.custom.ConflictException
import beyond.crud_sql.domain.User
import beyond.crud_sql.dto.request.CreateUserRequestDto
import beyond.crud_sql.dto.request.LoginRequestDto
import beyond.crud_sql.repository.UserRepository
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager

@SpringBootTest
@Transactional
class UserServiceTest @Autowired constructor(
    val userService: UserService,
    val userRepository: UserRepository,
    val em: EntityManager,
) {
    private val log = LoggerFactory.getLogger(UserService::class.java)

    lateinit var user1: User

    @BeforeEach
    fun setUp() {
        user1 = User("mouse1@naver.com", "1234", "실험쥐1")
        em.persist(user1)
        em.flush()
    }

    @Test
    fun createUser_success() {
        // given
        val request = CreateUserRequestDto("mouse2@naver.com", "1234", "실험쥐2")

        // when
        val result = userService.createUser(request)

        // then
        assertThat(user1.id == result.results.userId).isFalse
        assertThat(result.statusCode).isEqualTo(201)
        assertThat(result.message).isEqualTo("유저 등록에 성공하였습니다.")
    }

    @Test
    fun createUser_fail() {
        // given
        val request = CreateUserRequestDto(user1.email, user1.password, user1.nickname)

        // when + then
        assertThatThrownBy {
            userService.createUser(request)
        }.isInstanceOf(ConflictException::class.java).hasMessageContaining("이메일 혹은 닉네임 중복입니다.")
    }

    @Test
    fun getUser_success() {
        // given
        val request = LoginRequestDto(user1.email, user1.password)

        // when

        // then

    }


}