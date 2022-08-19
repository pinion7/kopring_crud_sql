package beyond.crud_sql.domain

import beyond.crud_sql.domain.base.BaseTimeEntity
import org.hibernate.annotations.GenericGenerator
import java.util.*
import javax.persistence.*


@Entity
@Table(name = "Users")
class User(email: String, password: String, nickname: String) : BaseTimeEntity() {

    @Id
    @GeneratedValue(generator = "uuid-gen", strategy = GenerationType.IDENTITY)
    @GenericGenerator(name = "uuid-gen", strategy = "uuid2")
    @Column(updatable = false)
    val id: UUID? = null

    @Column(unique = true)
    var email: String = email
        protected set;

    @Column
    var password: String = password
        protected set;

    @Column(unique = true)
    var nickname: String = nickname
        protected set;

    @OneToMany(mappedBy = "user")
    var posts: MutableList<Post> = mutableListOf()
        protected set;

    fun changeNickname(nickname: String) {
        this.nickname = nickname
    }

    override fun toString(): String {
        return "User(id=$id, email='$email', password='$password', nickname='$nickname', posts=$posts)"
    }
}