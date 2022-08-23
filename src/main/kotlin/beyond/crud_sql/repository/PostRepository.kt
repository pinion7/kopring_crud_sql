package beyond.crud_sql.repository

import beyond.crud_sql.domain.Post
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
interface PostRepository : JpaRepository<Post, UUID> {

    fun findByIdAndUserId(postId: UUID, userId: UUID): List<Post>

    fun deleteByIdAndUserId(postId: UUID, userId: UUID)
}