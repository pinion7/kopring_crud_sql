package beyond.crud_sql.domain

import beyond.crud_sql.domain.base.BaseTimeEntity
import org.hibernate.annotations.GenericGenerator
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "Users")
class User(email: String, password: String, nickname: String) : BaseTimeEntity() {

    @Id
    @GeneratedValue(generator = "uuid-gen")
    @GenericGenerator(name = "uuid-gen", strategy = "uuid2")
    @Column(nullable = false, updatable = false)
    var id: UUID? = null
        private set;

    @Column(unique = true, nullable = false)
    var email: String = email
        private set;

    @Column(nullable = false)
    var password: String = password
        private set;

    @Column(unique = true, nullable = false)
    var nickname: String = nickname
        private set;

    @OneToMany(mappedBy = "user")
    var posts: MutableList<Post> = mutableListOf()
        private set;

    fun changeNickname(nickname: String) {
        this.nickname = nickname
    }

    override fun toString(): String {
        return "User(id=$id, email='$email', password='$password', nickname='$nickname', posts=$posts)"
    }


}