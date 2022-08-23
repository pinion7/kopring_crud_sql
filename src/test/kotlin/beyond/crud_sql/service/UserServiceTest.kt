package beyond.crud_sql.service

import beyond.crud_sql.common.exception.custom.NotFoundException
import beyond.crud_sql.common.provider.JwtTokenProvider
import beyond.crud_sql.domain.User
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.util.*
import javax.persistence.EntityManager

@SpringBootTest
@Transactional
class UserServiceTest @Autowired constructor(
    val userService: UserService,
    val em: EntityManager,
    val jwtTokenProvider: JwtTokenProvider,
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
        // given + when
        val result = userService.createUser("mouse2@naver.com", "1234", "실험쥐2")

        // then
        assertThat(user1.id == result.results.userId).isFalse
        assertThat(result.statusCode).isEqualTo(201)
        assertThat(result.message).isEqualTo("회원 등록에 성공하였습니다.")
    }

    @Test
    fun getUserAndToken_success() {
        // given + when
        val result = userService.getUserAndToken(user1.email, user1.password)
        val (userId, email, nickname, createdDate, lastModifiedDate, accessToken) = result.results
        val claims = jwtTokenProvider.verifyAccessToken("${jwtTokenProvider.prefix} $accessToken")

        // then
        assertThat(user1.id).isEqualTo(userId)
        assertThat(user1.email).isEqualTo(email)
        assertThat(user1.nickname).isEqualTo(nickname)
        assertThat(user1.quit).isEqualTo(false)
        assertThat(user1.createdDate).isEqualTo(createdDate)
        assertThat(user1.lastModifiedDate).isEqualTo(lastModifiedDate)
        assertThat(claims?.issuer).isEqualTo("admin")
        assertThat(claims?.get("id")).isEqualTo(userId.toString())
        assertThat(claims?.get("email")).isEqualTo(user1.email)
    }

    @Test
    fun getUserAndToken_fail_404() {
        // given + when + then
        assertThatThrownBy {
            userService.getUserAndToken("test@naver.com", user1.password)
        }.isInstanceOf(NotFoundException::class.java).hasMessageContaining("존재하지 않는 이메일 입니다.")

        assertThatThrownBy {
            userService.getUserAndToken(user1.email, "incorrect")
        }.isInstanceOf(NotFoundException::class.java).hasMessageContaining("비밀번호가 일치하지 않습니다.")
    }

    @Test
    fun getUser_success() {
        // given + when
        val result = userService.getUser(user1.id!!)

        // then
        assertThat(result.results.userId).isEqualTo(user1.id)
        assertThat(result.results.email).isEqualTo(user1.email)
        assertThat(result.results.nickname).isEqualTo(user1.nickname)
        assertThat(result.results.quit).isEqualTo(user1.quit)
        assertThat(result.results.createdDate).isEqualTo(user1.createdDate)
        assertThat(result.results.lastModifiedDate).isEqualTo(user1.lastModifiedDate)
        assertThat(result.statusCode).isEqualTo(200)
        assertThat(result.message).isEqualTo("회원 정보 조회가 완료되었습니다.")
    }

    @Test
    fun getUser_fail_404() {
        // when + then
        assertThatThrownBy {
            userService.getUser(UUID.randomUUID())
        }.isInstanceOf(NotFoundException::class.java).hasMessageContaining("존재하지 않는 유저 입니다.")
    }

    @Test
    fun updateUser_success() {
        // given + when
        val result = userService.updateUser(user1, "새로운 닉네임")

        // then
        assertThat(result.results.userId).isEqualTo(user1.id)
        assertThat(result.statusCode).isEqualTo(200)
        assertThat(result.message).isEqualTo("회원 정보 수정이 완료되었습니다.")
    }

    @Test
    fun deleteUser_success() {
        // given
        val result = userService.deleteUser(user1)

        // when + then
        assertThat(result.results.userId).isEqualTo(user1.id)
        assertThat(result.results.quit).isEqualTo(true)
        assertThat(result.statusCode).isEqualTo(200)
        assertThat(result.message).isEqualTo("회원 탈퇴가 완료되었습니다.")
    }
}