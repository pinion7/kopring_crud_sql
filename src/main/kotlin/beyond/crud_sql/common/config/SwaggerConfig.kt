package beyond.crud_sql.common.config
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springdoc.core.GroupedOpenApi
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

@OpenAPIDefinition(info = Info(title = "Beyond 게시판 API 명세서", description = "Beyond 게시판 API 명세 및 테스트 툴입니다.", version = "v1"))
@Configuration
@PropertySource("classpath:/env.properties")
class SwaggerConfig {

    @Value("\${jwt.prefix}")
    lateinit var prefix: String

    @Bean
    fun publicApi(): GroupedOpenApi? {
        return GroupedOpenApi
            .builder()
            .group("Beyond-Practice")
            .pathsToMatch("/**")
            .build()
    }

    @Bean
    fun openAPI(): OpenAPI? {
        return OpenAPI()
            .components(
                Components()
                    .addSecuritySchemes(
                        "jwt token", SecurityScheme()
                            .description("token Access")
                            .type(SecurityScheme.Type.HTTP)
                            .`in`(SecurityScheme.In.HEADER)
                            .name("Authorization")
                            .bearerFormat("JWT")
                            .scheme(prefix),
                    )
            )
            .addSecurityItem(SecurityRequirement().addList("jwt token"))
    }
}