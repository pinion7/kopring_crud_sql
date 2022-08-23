package beyond.crud_sql.service

import beyond.crud_sql.common.exception.custom.NotFoundException
import beyond.crud_sql.domain.Post
import beyond.crud_sql.domain.User
import beyond.crud_sql.dto.request.UpdatePostRequestDto
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import javax.persistence.EntityManager


@SpringBootTest
@Transactional
class PostServiceTest @Autowired constructor(
    private val postService: PostService,
    private val em: EntityManager,
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
        val updateParams = UpdatePostRequestDto(null, "게시글 수정을 합시다.")

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
        val updateParams = UpdatePostRequestDto("제목 수정해봅니다.", null)

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
}