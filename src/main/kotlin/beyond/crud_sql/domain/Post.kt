package beyond.crud_sql.domain

import beyond.crud_sql.domain.base.BaseTimeEntity
import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.GenericGenerator
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "Posts")
class Post(
    @Column
    val title: String,

    @Column
    val content: String,

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,

    @Id
    @GeneratedValue(generator = "uuid-gen", strategy = GenerationType.IDENTITY)
    @GenericGenerator(name = "uuid-gen", strategy = "uuid2")
    @Column(nullable = false, updatable = false)
    val id: UUID? = null
) : BaseTimeEntity() {

}