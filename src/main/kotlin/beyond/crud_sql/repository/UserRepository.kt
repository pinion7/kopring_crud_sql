package beyond.crud_sql.repository

import beyond.crud_sql.domain.Post
import beyond.crud_sql.domain.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID


@Repository
interface UserRepository : JpaRepository<User, UUID>, UserQuerydslRepository {

    fun findByEmailAndQuit(email: String, quit: Boolean): MutableList<User>

    fun findByIdAndQuit(userId: UUID, quit: Boolean): MutableList<User>

    @Query(
        value = """select distinct u 
            from User u 
            join u.posts p 
            """
    )
    fun findUserPosts(): MutableList<User>

}

////language=json
//private val JSON_STRING = """
//        {
//          "name": "minsu",
//          "age": 35,
//          "add": "Asia/Seoul",
//
//        }
//    """.trimIndent()
