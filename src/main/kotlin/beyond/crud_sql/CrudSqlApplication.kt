package beyond.crud_sql

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class CrudSqlApplication

fun main(args: Array<String>) {
	runApplication<CrudSqlApplication>(*args)
}
