package beyond.crud_sql.service

import beyond.crud_sql.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(val userRepository: UserRepository) {

    fun createUser() {
    }
}