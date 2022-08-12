package beyond.crud_sql

import beyond.crud_sql.domain.User
import beyond.crud_sql.repository.UserRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class CrudSqlApplicationTests {


	@Autowired
	lateinit var userRepository: UserRepository;

	@Test
	fun contextLoads() {
		val user = User("easdf", "123", "boba");
		val saveUser = userRepository.save(user)

		val result = userRepository.findById(saveUser.id!!)
	}

}
