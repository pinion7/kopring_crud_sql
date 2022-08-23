package beyond.crud_sql.common.interceptor

import beyond.crud_sql.common.exception.custom.NotFoundException
import beyond.crud_sql.common.exception.custom.UnauthorizedException
import beyond.crud_sql.common.provider.JwtTokenProvider
import beyond.crud_sql.repository.UserRepository
import beyond.crud_sql.service.UserService
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpHeaders
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import java.util.UUID
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class LoginInterceptor(
    private val jwtTokenProvider: JwtTokenProvider,
    private val userRepository: UserRepository,
) : HandlerInterceptor {

    private val log = LoggerFactory.getLogger(UserService::class.java)

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val requestURI = request.requestURI
        log.info("preHandle [{}][{}]", request.dispatcherType, requestURI)

        val method = request.method
        if (method == "GET") {
            return true
        }

        val authorization = getAuthorization(request)
        log.info("authorization = {}", authorization)
        try {
            val claims = jwtTokenProvider.verifyAccessToken(authorization)
            log.info("claims = {}", claims.toString())
            setUser(request, claims?.get("id") as String)
            return true
        } catch (e: ExpiredJwtException) {
            log.error("ExpiredJwtException", e)
            throw UnauthorizedException("만료된 토큰입니다.")
        } catch (e: MalformedJwtException) {
            log.error("MalformedJwtException", e)
            throw UnauthorizedException("토큰 검증에 실패하였습니다.")
        }
    }

    private fun getAuthorization(request: HttpServletRequest): String {
        return request.getHeader(HttpHeaders.AUTHORIZATION)
            ?: throw IllegalArgumentException("헤더에 Authorization이 누락되었습니다.")
    }

    private fun setUser(request: HttpServletRequest, userId: String) {
        val user = userRepository.findByIdAndQuit(UUID.fromString(userId), false)
        if (user.isEmpty()) {
            throw NotFoundException("존재하지 않는 유저 입니다.")
        }
        request.setAttribute("User", user[0])
    }

    override fun postHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        modelAndView: ModelAndView?,
    ) {
        val requestURI = request.requestURI
        log.info("postHandle [{}][{}]", request.dispatcherType, requestURI)
    }

    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?,
    ) {
        val requestURI = request.requestURI
        log.info("afterCompletion [{}][{}]", request.dispatcherType, requestURI)
    }
}