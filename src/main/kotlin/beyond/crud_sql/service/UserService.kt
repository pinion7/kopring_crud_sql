package beyond.crud_sql.service

import beyond.crud_sql.common.custom.ConflictException
import beyond.crud_sql.common.custom.NotFoundException
import beyond.crud_sql.common.provider.JwtTokenProvider
import beyond.crud_sql.domain.User
import beyond.crud_sql.dto.request.CreateUserRequestDto
import beyond.crud_sql.dto.response.ResponseDto
import beyond.crud_sql.dto.result.CreateUserResultDto
import beyond.crud_sql.dto.result.GetLoginResultDto
import beyond.crud_sql.dto.result.GetUserResultDto
import beyond.crud_sql.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class UserService(val userRepository: UserRepository, val jwtTokenProvider: JwtTokenProvider) {

    private val log = LoggerFactory.getLogger(UserService::class.java)

    @Transactional
    fun createUser(params: CreateUserRequestDto): ResponseDto<CreateUserResultDto> {
        val (email, password, nickname) = params
        val createUser = User(email, password, nickname)

        try {
            val savedUser = userRepository.saveAndFlush(createUser)
            return ResponseDto(CreateUserResultDto(savedUser.id!!), 201, "유저 등록에 성공하였습니다.")
        } catch (e: DataIntegrityViolationException) {
            throw ConflictException("이메일 혹은 닉네임 중복입니다.", e.cause)
        }
    }

    fun getUserAndToken(email: String, password: String): ResponseDto<GetLoginResultDto> {
        val result = findUserByEmail(email)
        checkUserPassword(password, result.password)

        val token = jwtTokenProvider.makeJwtToken(result)
        return ResponseDto(
            GetLoginResultDto(result.id!!, result.email, result.nickname, result.createdDate, result.lastModifiedDate, token),
            200,
            "로그인에 성공하였습니다."
        )
    }

    fun getUser(userId: UUID): ResponseDto<GetUserResultDto> {
        val result = findUserById(userId)
        return ResponseDto(
            GetUserResultDto(result.id!!, result.email, result.nickname, result.createdDate, result.lastModifiedDate),
            200,
            "유저 정보 조회가 완료되었습니다."
        )
    }

    private fun checkUserPassword(originPassword: String, requestPassword: String) {
        if (originPassword != requestPassword) {
            throw NotFoundException("비밀번호가 일치하지 않습니다.")
        }
    }

    private fun findUserByEmail(email: String): User {
        val result = userRepository.findByEmail(email)
        if (result.isEmpty()) {
            throw NotFoundException("존재하지 않는 이메일 입니다.")
        }
        return result[0]
    }

    private fun findUserById(userId: UUID): User {
        return userRepository.findByIdOrNull(userId) ?: throw NotFoundException("존재하지 않는 유저 입니다.")
    }
}
