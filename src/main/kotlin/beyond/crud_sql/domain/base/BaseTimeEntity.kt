package beyond.crud_sql.domain.base

import beyond.crud_sql.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.time.ZoneId
import javax.persistence.*

private val log = LoggerFactory.getLogger(UserService::class.java)

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseTimeEntity {


    @CreatedDate
    @Column(updatable = false, nullable = false)
    var createdDate: LocalDateTime? = null
        protected set

    @LastModifiedDate
    @Column(nullable = false)
    var lastModifiedDate: LocalDateTime? = null
        protected set

//    @PrePersist
//    fun preCreate() {
//        log.info("prePersist createDate={}",createdDate.toString())
//        log.info("prePersist lastModifiedDate={}",lastModifiedDate.toString())
//        createdDate = LocalDateTime.now()
//        lastModifiedDate = createdDate
//    }
//
//    @PreUpdate
//    fun preUpdate() {
//        log.info("preUpdate createDate={}", createdDate.toString())
//        log.info("preUpdate lastModifiedDate={}", lastModifiedDate.toString())
////        lastModifiedDate = createdDate
//    }
}