package beyond.crud_sql.common.config

import beyond.crud_sql.common.interceptor.LoginInterceptor
import beyond.crud_sql.common.provider.JwtTokenProvider
import beyond.crud_sql.common.resolver.RestArgumentResolver
import beyond.crud_sql.repository.UserRepository
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    private val environment: Environment,
    private val userRepository: UserRepository
) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(LoginInterceptor(JwtTokenProvider(environment), userRepository))
            .order(1)
            .addPathPatterns("/**")
            .excludePathPatterns(
                "/auth/login/**",
                "/users",
            )
    }

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver?>) {
        resolvers.add(RestArgumentResolver())
    }
}