package beyond.crud_sql.common.database

import beyond.crud_sql.domain.Post
import beyond.crud_sql.domain.User
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.lang.Thread.*
import java.util.*
import javax.annotation.PostConstruct
import javax.persistence.EntityManager

@Component
class InitData(private val initService: InitService) {

    @PostConstruct
    fun init() {
        initService.dbInit1()
    }

    companion object {
        @Component
        @Transactional
        class InitService(
            private val em: EntityManager
        ) {
            private val fakerId = UUID.fromString("3ede0ef2-92b7-4817-a5f3-0c575361f745");
            private val faker: User = User("faker@t1.com", "Faker", "페이커")
            private val bdd: User = User("bdd@ns.com", "Bdd", "비디디")
            private val showmaker: User = User("showmaker@dk.com", "Showmaker", "쇼메이커")
            private val chovy: User = User("chovy@geng.com", "Chovy", "쵸비")
            private val faker2013: Post = Post("2013 월즈 우승 with 티원", "T1 - 임팩트, 벵기, 페이커, 피글렛, 푸만두 우승!", faker)
            private val faker2015: Post = Post("2015 월즈 우승 with 티원", "T1 - 마린, 벵기, 페이커(이지훈), 뱅, 울프 우승!", faker)
            private val faker2016: Post = Post("2016 월즈 우승 with 티원", "T1 - 듀크, 벵기(블랭크), 페이커, 뱅, 울프 우승!", faker)
            private val faker2017: Post = Post("2017 월즈 준우승 with 티원", "T1 - 후니(운타라), 피넛(블랭크), 페이커, 뱅, 울프 준우승!", faker)
            private val faker2019: Post = Post("2019 월즈 4강 with 티원", "T1 - 칸, 클리드, 페이커, 테디, 에포트 4강!.", faker)
            private val faker2021: Post = Post("2021 월즈 4강 with 티원", "T1 - 칸나, 오너, 페이커, 구마유시(테디), 케리아 4강!", faker)
            private val faker2022: Post = Post("2022 월즈 도전 with 티원", "T1 - 제우스, 오너, 페이커, 구마유시, 케리아 도전!", faker)
            private val bdd2017: Post = Post("2017 월즈 8강 with 롱쥬", "LongZu - 칸, 커즈, 비디디, 프레이, 고릴라와 함께 8강!", bdd)
            private val bdd2020: Post = Post("2020 월즈 8강 with 젠지", "GenG - 라스칼, 클리드, 비디디, 룰러, 라이프와 함께 8강!", bdd)
            private val bdd2021: Post = Post("2021 월즈 4강 with 젠지", "GenG - 라스칼, 클리드, 비디디, 룰러, 라이프와 함께 4강!", bdd)
            private val showmaker2019: Post = Post("2019 월즈 8강 with 담원", "Damwon - 너구리, 캐니언, 쇼메이커, 뉴클리어, 베릴 8강!", showmaker)
            private val showmaker2020: Post = Post("2020 월즈 우승 with 담원", "Damwon - 너구리, 캐니언, 쇼메이커, 고스트, 베릴 우승!", showmaker)
            private val showmaker2021: Post = Post("2021 월즈 준우승 with 담원", "Damwon - 칸, 캐니언, 쇼메이커, 고스트, 베릴 준우승!", showmaker)
            private val showmaker2022: Post = Post("2022 월즈 도전 with 담원", "Damwon - 너구리(버돌), 캐니언, 쇼메이커, 덕담, 켈린 도전!", showmaker)
            private val chovy2019: Post = Post("2019 월즈 8강 with 그리핀", "Griffin - 소드(도란), 타잔, 쵸비, 바이퍼, 리헨즈와 함께 8강!", chovy)
            private val chovy2020: Post = Post("2020 월즈 8강 with 드락스", "Drx - 도란, 표식, 쵸비, 데프트, 케리아 8강!", chovy)
            private val chovy2021: Post = Post("2021 월즈 8강 with 한화", "Hanhwa - 모건(두두), 윌러, 쵸비, 데프트, 비스타 8강!", chovy)
            private val chovy2022: Post = Post("2022 월즈 도전 with 젠지", "GenG - 도란, 피넛, 쵸비, 룰러, 리헨즈 도전!", chovy)

            fun dbInit1() {
                // 유저 등록
                em.persist(faker)
                em.persist(bdd)
                em.persist(showmaker)
                em.persist(chovy)

                // 포스트 등록
                em.persist(faker2013)
                sleep(10)
                em.persist(faker2015)
                sleep(10)
                em.persist(faker2016)
                sleep(10)
                em.persist(bdd2017)
                sleep(10)
                em.persist(faker2017)
                sleep(10)
                em.persist(chovy2019)
                sleep(10)
                em.persist(showmaker2019)
                sleep(10)
                em.persist(faker2019)
                sleep(10)
                em.persist(bdd2020)
                sleep(10)
                em.persist(chovy2020)
                sleep(10)
                em.persist(showmaker2020)
                sleep(10)
                em.persist(chovy2021)
                sleep(10)
                em.persist(bdd2021)
                sleep(10)
                em.persist(faker2021)
                sleep(10)
                em.persist(showmaker2021)
                sleep(10)
                em.persist(showmaker2022)
                sleep(10)
                em.persist(faker2022)
                sleep(10)
                em.persist(chovy2022)
            }
        }
    }
}