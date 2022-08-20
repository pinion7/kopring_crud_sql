package beyond.crud_sql.common.config

import beyond.crud_sql.common.interceptor.LoginInterceptor
import beyond.crud_sql.common.provider.JwtTokenProvider
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(private val environment: Environment) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(LoginInterceptor(JwtTokenProvider(environment)))
            .order(1)
            .addPathPatterns("/**")
            .excludePathPatterns(
                "/auth/login/**",
                "/users",
            )
    }
}