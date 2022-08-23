package beyond.crud_sql.repository

import beyond.crud_sql.domain.QPost.post
import beyond.crud_sql.domain.QUser.user
import beyond.crud_sql.dto.condition.PostSearchCondition
import beyond.crud_sql.dto.show.PostShowDto
import beyond.crud_sql.dto.show.QPostShowDto
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository

@Repository
class PostRepositoryCustomImpl(
    private val queryFactory: JPAQueryFactory,
) : PostRepositoryCustom {

    override fun findPostListWithCondition(condition: PostSearchCondition, pageable: Pageable): Page<PostShowDto> {
        val content = getPostListShowDto(condition, pageable)
        val count = getCountQuery(condition)

        return PageableExecutionUtils.getPage(content, pageable) { count.fetchOne()!! }
    }

    private fun getPostListShowDto(
        condition: PostSearchCondition,
        pageable: Pageable,
    ): MutableList<PostShowDto> {
        return queryFactory
            .select(QPostShowDto(post))
            .distinct()
            .from(post)
            .leftJoin(post.user, user).fetchJoin()
            .where(
                post.user.nickname.contains(condition.writer ?: "")
                    .and(post.title.contains(condition.title ?: ""))
                    .and(post.content.contains(condition.content ?: ""))
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(post.createdDate.desc())
            .fetch()
    }

    private fun getCountQuery(condition: PostSearchCondition): JPAQuery<Long> {
        return queryFactory
            .select(post.count())
            .from(post)
            .leftJoin(post.user, user)
            .where(
                post.user.nickname.contains(condition.writer ?: "")
                    .and(post.title.contains(condition.title ?: ""))
                    .and(post.content.contains(condition.content ?: ""))
//                condition.writer?.let { user.nickname.contains(it) },
//                condition.title?.let { title -> post.title.contains(title) },
//                condition.content?.let { post.content.contains(it) }
            )
    }
}