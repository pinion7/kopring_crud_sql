package beyond.crud_sql.common.config
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springdoc.core.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@OpenAPIDefinition(info = Info(title = "Beyond 게시판 API 명세서", description = "Beyond 게시판 API 명세 및 테스트 툴입니다.", version = "v1"))
@Configuration
class SwaggerConfig {

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
                        "Prefix_Token", SecurityScheme()
                            .type(SecurityScheme.Type.APIKEY)
                            .description("Prefix Token Access")
                            .`in`(SecurityScheme.In.HEADER)
                            .name("Authorization")
                    )
            )
            .addSecurityItem(SecurityRequirement().addList("Prefix_Token"))
    }
}