package beyond.crud_sql.repository

import beyond.crud_sql.domain.Post
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PostRepository : JpaRepository<Post, UUID>, PostQuerydslRepository {

    fun findByIdAndUserId(postId: UUID, userId: UUID): List<Post>

    fun deleteByIdAndUserId(postId: UUID, userId: UUID)

    @Query(
        value = "select distinct p from Post p " +
                "join fetch p.user u " +
                "order by p.createdDate desc",
        countQuery = "select count(p) from Post p"
    )
    fun findPostAll(pageable: Pageable): Page<Post>

    @Query(
        value = "select distinct p from Post p " +
                "join fetch p.user u " +
                "where p.user.id = :userId",
        countQuery = "select count(p) from Post p where p.user.id = :userId"
    )
    fun findPostAllByUserId(@Param("userId") userId: UUID, pageable: Pageable): Page<Post>
}