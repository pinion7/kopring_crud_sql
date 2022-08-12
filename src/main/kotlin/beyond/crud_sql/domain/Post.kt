package beyond.crud_sql.domain

import beyond.crud_sql.domain.base.BaseTimeEntity
import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.GenericGenerator
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "Posts")
class Post(title: String, content: String, user: User) : BaseTimeEntity() {

    @Id
    @GeneratedValue(generator = "uuid-gen")
    @GenericGenerator(name = "uuid-gen", strategy = "uuid2")
    @Column(nullable = false, updatable = false)
    var id: UUID? = null
        protected set;

    @Column(nullable = false)
    var title: String = title
        protected set;

    @Column(nullable = false)
    var content: String = content
        protected set;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User = user
        protected set;
}