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

        log.info("path [{}]", request.servletPath)
        log.info("path [{}]", request.requestedSessionId)
        log.info("path [{}]", request.queryString)
        log.info("path [{}]", request.session)
        log.info("path [{}]", request.httpServletMapping)

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
            throw UnauthorizedException("????????? ???????????????.")
        } catch (e: MalformedJwtException) {
            log.error("MalformedJwtException", e)
            throw UnauthorizedException("?????? ????????? ?????????????????????.")
        }
    }

    private fun getAuthorization(request: HttpServletRequest): String {
        return request.getHeader(HttpHeaders.AUTHORIZATION)
            ?: throw IllegalArgumentException("????????? Authorization??? ?????????????????????.")
    }

    private fun setUser(request: HttpServletRequest, userId: String) {
        val user = userRepository.findByIdAndQuit(UUID.fromString(userId), false)
        if (user.isEmpty()) {
            throw NotFoundException("???????????? ?????? ?????? ?????????.")
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