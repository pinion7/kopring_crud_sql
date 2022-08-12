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
        protected set;

    @Column(nullable = false)
    var email: String = email
        protected set;

    @Column(nullable = false)
    var password: String = password
        protected set;

    @Column(nullable = false)
    var nickname: String = nickname
        protected set;

    @OneToMany(mappedBy = "user")
    var posts: MutableList<Post> = mutableListOf()
        protected set;
}