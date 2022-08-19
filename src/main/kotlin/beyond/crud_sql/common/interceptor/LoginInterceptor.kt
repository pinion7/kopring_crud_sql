package beyond.crud_sql.common.interceptor

import beyond.crud_sql.common.custom.UnauthorizedException
import beyond.crud_sql.common.provider.JwtTokenProvider
import beyond.crud_sql.service.UserService
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class LoginInterceptor(private val jwtTokenProvider: JwtTokenProvider) : HandlerInterceptor {

    private val log = LoggerFactory.getLogger(UserService::class.java)

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val requestURI = request.requestURI
        log.info("preHandle [{}][{}]", request.dispatcherType, requestURI)

        val method = request.method
        if (method == "GET") {
            return true
        }

        val authorization = request.getHeader(HttpHeaders.AUTHORIZATION)
        log.info(authorization)
        try {
            val claims = jwtTokenProvider.parseJwtToken(authorization)
            log.info(claims.toString())
            return true
        } catch (e: ExpiredJwtException) {
            log.error("ExpiredJwtException", e)
            throw UnauthorizedException("만료된 토큰입니다.")
        } catch (e: MalformedJwtException) {
            log.error("MalformedJwtException", e)
            throw UnauthorizedException("토큰 검증에 실패하였습니다.")
        }
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