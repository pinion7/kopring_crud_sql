package beyond.crud_sql.common.provider

import beyond.crud_sql.domain.User
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Header
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.*

@Component
class JwtTokenProvider {

    private val issuer = "admin"
    private val secret = "secret"
    private val preFix = "Bearer "

    fun makeJwtToken(user: User): String {
        val now = Date()

        return Jwts.builder()
            .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
            .setIssuer(issuer)
            .setIssuedAt(now)
            .setExpiration(Date(now.time + Duration.ofMinutes(30).toMillis()))
            .claim("id", user.id)
            .claim("email", user.email)
            .signWith(SignatureAlgorithm.HS256, secret)
            .compact()
    }

    fun parseJwtToken(authorizationHeader: String): Claims? {
        validationAuthorizationHeader(authorizationHeader)
        val token = extractToken(authorizationHeader)

        return Jwts.parser()
            .setSigningKey(secret)
            .parseClaimsJws(token)
            .body
    }

    private fun validationAuthorizationHeader(header: String) {
        if (!header.startsWith(preFix)) {
          throw IllegalArgumentException()
        }
    }

    private fun extractToken(authorizationHeader: String): String {
        return authorizationHeader.substring(preFix.length)
    }
}