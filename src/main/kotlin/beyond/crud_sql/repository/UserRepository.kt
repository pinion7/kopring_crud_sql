package beyond.crud_sql.repository

import beyond.crud_sql.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID


@Repository
interface UserRepository : JpaRepository<User, UUID> {

    fun findByEmail(email: String): List<User>
}