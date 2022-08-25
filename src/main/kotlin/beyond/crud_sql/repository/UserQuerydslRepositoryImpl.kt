package beyond.crud_sql.repository

import beyond.crud_sql.domain.QUser.user
import beyond.crud_sql.dto.condition.UserSearchCondition
import beyond.crud_sql.dto.show.QSearchUserShowDto
import beyond.crud_sql.dto.show.SearchUserShowDto
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository

@Repository
class UserQuerydslRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : UserQuerydslRepository {

    override fun findUserAllWithCondition(condition: UserSearchCondition, pageable: Pageable): Page<SearchUserShowDto> {
        val content = getUserShowDtoAll(condition, pageable)
        val count = getCountQuery(condition)

        return PageableExecutionUtils.getPage(content, pageable) { count.fetchOne()!! }
    }

    private fun getUserShowDtoAll(
        condition: UserSearchCondition,
        pageable: Pageable,
    ): MutableList<SearchUserShowDto> {
        return queryFactory
            .select(QSearchUserShowDto(user))
            .distinct()
            .from(user)
            .where(
                user.email.contains(condition.email ?: "")
                    .and(user.nickname.contains(condition.nickname ?: ""))
                    .and(user.quit.eq(false))
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(user.createdDate.desc())
            .fetch()
    }

    private fun getCountQuery(condition: UserSearchCondition): JPAQuery<Long> {
        return queryFactory
            .select(user.count())
            .from(user)
            .where(
                user.email.contains(condition.email ?: "")
                    .and(user.nickname.contains(condition.nickname ?: ""))
                    .and(user.quit.eq(false))
            )
    }
}