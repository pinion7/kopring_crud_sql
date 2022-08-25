package beyond.crud_sql.domain.base

import beyond.crud_sql.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.persistence.*


private val log = LoggerFactory.getLogger(UserService::class.java)

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseTimeEntity {

    @CreatedDate
    @Column(updatable = false, nullable = false)
    var createdDate: String? = null
        protected set

    @LastModifiedDate
    @Column(nullable = false)
    var lastModifiedDate: String? = null
        protected set

    @PrePersist
    fun onPrePersist() {
        createdDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        lastModifiedDate = createdDate
    }

    @PreUpdate
    fun onPreUpdate() {
        lastModifiedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    }
}