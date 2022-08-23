package beyond.crud_sql.domain

import beyond.crud_sql.domain.base.BaseTimeEntity
import beyond.crud_sql.dto.request.UpdatePostRequestDto
import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.GenericGenerator
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "Posts")
class Post(title: String, content: String, user: User) : BaseTimeEntity() {

    @Id
    @GeneratedValue(generator = "uuid-gen", strategy = GenerationType.IDENTITY)
    @GenericGenerator(name = "uuid-gen", strategy = "uuid2")
    @Column(updatable = false)
    val id: UUID? = null

    @Column
    var writer: String = user.nickname
        protected set

    @Column
    var title: String = title
        protected set

    @Column
    var content: String = content
        protected set

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: User = user
        protected set

    fun updatePostFields(params: UpdatePostRequestDto) {
        val (title, content) = params
        if (title != null) this.title = title
        if (content != null) this.content = content
    }
}