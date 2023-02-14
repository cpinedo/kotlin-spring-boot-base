package es.cpinedo.security.infrastructure.adapters

import es.cpinedo.core.domain.CoreException
import es.cpinedo.core.domain.ErrorType
import es.cpinedo.security.application.ports.JwtUtils
import es.cpinedo.security.domain.JwtToken
import es.cpinedo.security.infrastructure.configuration.SecurityConfigurationValues
import io.jsonwebtoken.*
import mu.KotlinLogging
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component
import java.security.Key
import java.security.SignatureException
import java.util.*
import javax.annotation.PostConstruct
import javax.crypto.spec.SecretKeySpec
import javax.xml.bind.DatatypeConverter

@Component
class JwtUtilsImpl(
    private val userDetailsServiceImpl: UserDetailsService,
    private val configurationValues: SecurityConfigurationValues
): JwtUtils {

    private val logger = KotlinLogging.logger {}

    private var secret: String = ""

    @PostConstruct
    fun initialize() {
        secret = Base64.getEncoder().encodeToString(configurationValues.jwtRawSecret!!.encodeToByteArray())
    }

    override fun generateTokenFromUsername(username: String): JwtToken {
        val userDetails = userDetailsServiceImpl.loadUserByUsername(username)
        val roles = userDetails.authorities.map { ga -> ga.authority }
        return buildToken(userDetails.username, roles.toTypedArray())
    }

    override fun getUserNameFromJwtToken(token: String?): String {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).body.subject
    }

    override fun getUserNameFromExpiredJwtToken(token: String): String {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token).body.subject
            throw TokenNotExpiredYetException()
        } catch (e: ExpiredJwtException) {
            return e.claims.subject
        }
    }

    override fun validateJwtToken(authToken: String?): Boolean {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(authToken)
            return true
        } catch (e: SignatureException) {
            logger.error("Invalid JWT signature: {}", e.message)
        } catch (e: MalformedJwtException) {
            logger.error("Invalid JWT token: {}", e.message)
        } catch (e: ExpiredJwtException) {
            logger.error("JWT token is expired: {}", e.message)
        } catch (e: UnsupportedJwtException) {
            logger.error("JWT token is unsupported: {}", e.message)
        } catch (e: IllegalArgumentException) {
            logger.error("JWT claims string is empty: {}", e.message)
        }
        return false
    }

    override fun buildToken(username: String, grantedAuthorities: Array<String>): JwtToken {
        val apiKeySecretBytes =
            DatatypeConverter.parseBase64Binary(secret)
        val signingKey: Key = SecretKeySpec(apiKeySecretBytes, SignatureAlgorithm.HS512.jcaName)

        val expirationTime = Date(Date().time + configurationValues.jwtExpirationMs)

        return JwtToken(
            Jwts.builder()
                .setSubject(username)
                .setIssuer(configurationValues.jwtIssuer)
                .setIssuedAt(Date())
                .setExpiration(expirationTime)
                .claim("authorities", grantedAuthorities)
                .signWith(SignatureAlgorithm.HS512, signingKey)
                .compact(), expirationTime.time
        )
    }

    class TokenNotExpiredYetException : CoreException("The token didn't expire yet", ErrorType.CONFLICT)

}
