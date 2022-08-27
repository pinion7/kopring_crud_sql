package beyond.crud_sql.service

import beyond.crud_sql.common.exception.custom.NotFoundException
import beyond.crud_sql.domain.Post
import beyond.crud_sql.domain.User
import beyond.crud_sql.dto.condition.PostSearchCondition
import beyond.crud_sql.dto.request.UpdatePostRequestDto
import beyond.crud_sql.repository.UserRepository
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import javax.persistence.EntityManager
import javax.validation.ConstraintViolationException


@SpringBootTest
@Transactional
class PostServiceTest @Autowired constructor(
    private val postService: PostService,
    private val em: EntityManager,
    private val userRepository: UserRepository
) {

    lateinit var user1: User
    lateinit var post1: Post

    @BeforeEach
    fun setUp() {
        user1 = User("mouse1@naver.com", "1234", "실험쥐1")
        em.persist(user1)

        post1 = Post("실험쥐1의 첫 게시글", "실험쥐1은 영민합니다.", user1)
        em.persist(post1)

        em.flush()
    }

    @Test
    fun creatPost_201() {
        // when
        val result = postService.createPost("실험쥐1의 두번째 글", "실험쥐1은 재빠릅니다.", user1)

        // then
        assertThat(result.results.userId).isEqualTo(user1.id)
        assertThat(result.statusCode).isEqualTo(201)
        assertThat(result.message).isEqualTo("게시글 작성이 완료되었습니다.")
    }

    @Test
    fun getPost_200() {
        // when
        val result = postService.getPost(post1.id!!)

        // then
        assertThat(result.results.postId).isEqualTo(post1.id)
        assertThat(result.results.userId).isEqualTo(user1.id)
        assertThat(result.results.writer).isEqualTo(user1.nickname)
        assertThat(result.results.title).isEqualTo(post1.title)
        assertThat(result.results.content).isEqualTo(post1.content)
        assertThat(result.results.createdDate).isEqualTo(post1.createdDate)
        assertThat(result.results.lastModifiedDate).isEqualTo(post1.lastModifiedDate)
        assertThat(result.statusCode).isEqualTo(200)
        assertThat(result.message).isEqualTo("게시글 조회가 완료되었습니다.")
    }

    @Test
    fun getPost_404() {
        // when + then
        assertThatThrownBy {
            postService.getPost(UUID.randomUUID())
        }.isInstanceOf(NotFoundException::class.java).hasMessageContaining("게시글을 찾을 수 없습니다.")
    }

    @Test
    fun updatePost_200() {
        // given
        val updateParams = UpdatePostRequestDto("", "게시글 수정을 합시다.")

        // when
        val result = postService.updatePost(post1.id!!, user1.id!!, updateParams)

        // then
        assertThat(result.results.postId).isEqualTo(post1.id)
        assertThat(result.results.userId).isEqualTo(user1.id)
        assertThat(result.statusCode).isEqualTo(200)
        assertThat(result.message).isEqualTo("게시글 수정이 완료되었습니다.")
    }

    @Test
    fun updatePost_404() {
        // given
        val updateParams = UpdatePostRequestDto("제목 수정해봅니다.", "")

        // when + then
        assertThatThrownBy {
            postService.updatePost(UUID.randomUUID(), user1.id!!, updateParams)
        }.isInstanceOf(NotFoundException::class.java).hasMessageContaining("게시글을 찾을 수 없습니다.")
    }

    @Test
    fun deletePost_200() {
        // when
        val result = postService.deletePost(post1.id!!, user1.id!!)

        // then
        assertThat(result.results.postId).isEqualTo(post1.id)
        assertThat(result.results.userId).isEqualTo(user1.id)
        assertThat(result.statusCode).isEqualTo(200)
        assertThat(result.message).isEqualTo("게시글 삭제가 완료되었습니다.")

        assertThatThrownBy {
            postService.getPost(post1.id!!)
        }.isInstanceOf(NotFoundException::class.java).hasMessageContaining("게시글을 찾을 수 없습니다.")
    }

    @Test
    fun getUserWithPostAll_200() {
        // given
        val faker = userRepository.findByEmailAndQuit("faker@t1.com", false)[0]
        val result = postService.getUserWithPostAll(faker.id!!, PageRequest.of(0, 2))
        val user = result.results

        // when + then
        assertThat(user.posts.size).isEqualTo(2)
        assertThat(user.posts[0].userId).isEqualTo(faker.id)
        assertThat(user.posts[0].writer).isEqualTo(faker.nickname)
        assertThat(user.posts[1].userId).isEqualTo(faker.id)
        assertThat(user.posts[1].writer).isEqualTo(faker.nickname)
        assertThat(user.totalPages).isEqualTo(4)
        assertThat(user.totalElements).isEqualTo(7)
        assertThat(user.numberOfElements).isEqualTo(2)
        assertThat(user.pageNumber).isEqualTo(0)
        assertThat(user.pageSize).isEqualTo(2)
        assertThat(user.isFirst).isTrue
        assertThat(user.isNext).isTrue
        assertThat(result.statusCode).isEqualTo(200)
        assertThat(result.message).isEqualTo("회원 게시글 조회가 완료되었습니다.")
    }

    @Test
    fun getUserWithPostAll_200_empty() {
        // given
        val result = postService.getUserWithPostAll(
            UUID.randomUUID(), PageRequest.of(0, 3)
        )
        val user = result.results

        // when + then
        assertThat(user.posts.size).isEqualTo(0)
        assertThat(user.totalPages).isEqualTo(0)
        assertThat(user.totalElements).isEqualTo(0)
        assertThat(user.numberOfElements).isEqualTo(0)
        assertThat(user.pageNumber).isEqualTo(0)
        assertThat(user.pageSize).isEqualTo(3)
        assertThat(user.isFirst).isTrue
        assertThat(user.isNext).isFalse
        assertThat(result.statusCode).isEqualTo(200)
        assertThat(result.message).isEqualTo("회원 게시글 조회가 완료되었습니다.")
    }


    @Test
    fun getPostAll_200() {
        // given
        val pageRequest = PageRequest.of(0, 3)
        val result = postService.getPostAll(pageRequest)
        val getPosts = result.results

        // when + then
        assertThat(getPosts.posts.size).isEqualTo(3)
        assertThat(getPosts.totalPages).isEqualTo(7)
        assertThat(getPosts.totalElements).isEqualTo(19)
        assertThat(getPosts.numberOfElements).isEqualTo(3)
        assertThat(getPosts.pageNumber).isEqualTo(0)
        assertThat(getPosts.pageSize).isEqualTo(3)
        assertThat(getPosts.isFirst).isTrue
        assertThat(getPosts.isNext).isTrue
        assertThat(result.statusCode).isEqualTo(200)
        assertThat(result.message).isEqualTo("게시글 리스트 조회가 완료되었습니다.")
    }

    @Test
    fun searchPostAll_200() {
        // given
        val condition = PostSearchCondition("이커", "티원", " 우승")
        val pageRequest = PageRequest.of(0, 2)
        val result = postService.searchPostAll(condition, pageRequest)
        val searchPost = result.results

        // when + then
        assertThat(searchPost.posts.size).isEqualTo(2)
        assertThat(searchPost.posts[0].writer).contains(condition.writer)
        assertThat(searchPost.posts[0].title).contains(condition.title)
        assertThat(searchPost.posts[0].content).contains(condition.content)
        assertThat(searchPost.posts[1].writer).contains(condition.writer)
        assertThat(searchPost.posts[1].title).contains(condition.title)
        assertThat(searchPost.posts[1].content).contains(condition.content)
        assertThat(searchPost.totalPages).isEqualTo(2)
        assertThat(searchPost.totalElements).isEqualTo(3)
        assertThat(searchPost.numberOfElements).isEqualTo(2)
        assertThat(searchPost.pageNumber).isEqualTo(0)
        assertThat(searchPost.pageSize).isEqualTo(2)
        assertThat(searchPost.isFirst).isTrue
        assertThat(searchPost.isNext).isTrue
        assertThat(result.statusCode).isEqualTo(200)
        assertThat(result.message).isEqualTo("게시글 리스트 조건 검색이 완료되었습니다.")
    }

    @Test
    fun searchPostAll_200_empty() {
        // given
        val condition = PostSearchCondition("없는유저", "티원", " 우승")
        val pageRequest = PageRequest.of(0, 2)
        val result = postService.searchPostAll(condition, pageRequest)
        val searchPost = result.results

        // when + then
        assertThat(searchPost.posts.size).isEqualTo(0)
        assertThat(searchPost.totalPages).isEqualTo(0)
        assertThat(searchPost.totalElements).isEqualTo(0)
        assertThat(searchPost.numberOfElements).isEqualTo(0)
        assertThat(searchPost.pageNumber).isEqualTo(0)
        assertThat(searchPost.pageSize).isEqualTo(2)
        assertThat(searchPost.isFirst).isTrue
        assertThat(searchPost.isNext).isFalse
        assertThat(result.statusCode).isEqualTo(200)
        assertThat(result.message).isEqualTo("게시글 리스트 조건 검색이 완료되었습니다.")
    }

}