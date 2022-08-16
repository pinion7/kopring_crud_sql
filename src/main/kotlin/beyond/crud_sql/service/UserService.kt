package beyond.crud_sql.service

import beyond.crud_sql.domain.User
import beyond.crud_sql.dto.CreateUserDto
import beyond.crud_sql.dto.CreateUserResultDto
import beyond.crud_sql.dto.response.ResponseDto
import beyond.crud_sql.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserService(val userRepository: UserRepository) {

    fun createUser(params: CreateUserDto): ResponseDto<CreateUserResultDto> {
        val (email, password, nickname) = params
        val createUser = User(email, password, nickname)

        val savedUser = userRepository.save(createUser)
        return ResponseDto(CreateUserResultDto(savedUser.id!!), 201, "유저 등록에 성공하였습니다.")
    }
}