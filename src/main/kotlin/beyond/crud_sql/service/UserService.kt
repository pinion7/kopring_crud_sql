package beyond.crud_sql.service

import beyond.crud_sql.common.exception.custom.NotFoundException
import beyond.crud_sql.common.provider.JwtTokenProvider
import beyond.crud_sql.domain.User
import beyond.crud_sql.dto.condition.PostSearchCondition
import beyond.crud_sql.dto.condition.UserSearchCondition
import beyond.crud_sql.dto.response.ResponseDto
import beyond.crud_sql.dto.result.*
import beyond.crud_sql.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
    private val jwtTokenProvider: JwtTokenProvider
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun createUser(email: String, password: String, nickname: String): ResponseDto<CreateUserResultDto> {
        val createUser = User(email, password, nickname)
        val savedUser = userRepository.save(createUser)

        return ResponseDto(
            CreateUserResultDto(savedUser.id!!),
            201,
            "회원 등록에 성공하였습니다."
        )
    }

    fun getUserAndToken(email: String, password: String): ResponseDto<GetLoginResultDto> {
        val result = findUserByEmail(email)
        checkUserPassword(password, result.password)

        val token = jwtTokenProvider.issueAccessToken(result)
        return ResponseDto(
            GetLoginResultDto(
                result.id!!,
                result.email,
                result.nickname,
                result.createdDate,
                result.lastModifiedDate,
                token
            ),
            200,
            "로그인에 성공하였습니다."
        )
    }

    fun getUser(userId: UUID): ResponseDto<GetUserResultDto> {
        val result = findUserById(userId)

        return ResponseDto(
            GetUserResultDto(
                result.id!!,
                result.email,
                result.nickname,
                result.quit,
                result.createdDate,
                result.lastModifiedDate
            ),
            200,
            "회원 정보 조회가 완료되었습니다."
        )
    }

    @Transactional
    fun updateUser(user: User, nickname: String): ResponseDto<UpdateUserResultDto> {
        user.updateNickname(nickname)

        return ResponseDto(
            UpdateUserResultDto(user.id!!),
            200,
            "회원 정보 수정이 완료되었습니다."
        )
    }

    @Transactional
    fun deleteUser(user: User): ResponseDto<DeleteUserResultDto> {
        user.withdraw()

        return ResponseDto(
            DeleteUserResultDto(user.id!!, user.quit),
            200,
            "회원 탈퇴가 완료되었습니다."
        )
    }

    fun searchUserAll(condition: UserSearchCondition, pageable: Pageable): ResponseDto<SearchUserAllResultDto> {
        val result = userRepository.findUserAllWithCondition(condition, pageable)
        return ResponseDto(
            SearchUserAllResultDto(result),
            200,
            "회원 리스트 조건 검색이 완료되었습니다."
        )
    }

    private fun checkUserPassword(originPassword: String, requestPassword: String) {
        if (originPassword != requestPassword) {
            throw NotFoundException("비밀번호가 일치하지 않습니다.")
        }
    }

    private fun findUserByEmail(email: String): User {
        val result = userRepository.findByEmailAndQuit(email, false)
        if (result.isEmpty()) {
            throw NotFoundException("존재하지 않는 이메일 입니다.")
        }
        return result[0]
    }

    private fun findUserById(userId: UUID): User {
        val result = userRepository.findByIdAndQuit(userId, false)
        if (result.isEmpty()) {
            throw NotFoundException("존재하지 않는 유저 입니다.")
        }
        return result[0]
    }
}
