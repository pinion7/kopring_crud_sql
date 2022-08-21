package beyond.crud_sql.common.provider

import beyond.crud_sql.domain.User
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Header
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.*

@Component
class JwtTokenProvider(environment: Environment) {

    val prefix = environment.getProperty("jwt.prefix")!!
    val secret = environment.getProperty("jwt.access.secret")!!
    val expiration = environment.getProperty("jwt.access.expiration")!!

    fun issueAccessToken(user: User): String {
        val now = Date()

        return Jwts.builder()
            .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
            .setIssuer("admin")
            .setIssuedAt(now)
            .setExpiration(Date(now.time + Duration.ofMinutes(expiration.toLong()).toMillis()))
            .claim("id", user.id)
            .claim("email", user.email)
            .signWith(SignatureAlgorithm.HS256, secret)
            .compact()
    }

    fun verifyAccessToken(authorization: String): Claims? {
        validatePrefix(authorization)
        val token = extractToken(authorization)

        return Jwts.parser()
            .setSigningKey(secret)
            .parseClaimsJws(token)
            .body
    }

    private fun validatePrefix(header: String) {
        if (!header.startsWith(prefix)) {
          throw IllegalArgumentException("잘못된 prefix 입니다.")
        }
    }

    private fun extractToken(authorization: String): String {
        return authorization.substring(prefix.length + 1)
    }
}