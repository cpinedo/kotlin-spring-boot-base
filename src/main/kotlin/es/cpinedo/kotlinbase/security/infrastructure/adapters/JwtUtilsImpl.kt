package es.cpinedo.kotlinbase.security.infrastructure.adapters

import es.cpinedo.kotlinbase.core.domain.CoreException
import es.cpinedo.kotlinbase.core.domain.ErrorType
import es.cpinedo.kotlinbase.security.application.ports.JwtUtils
import es.cpinedo.kotlinbase.security.domain.JwtToken
import es.cpinedo.kotlinbase.security.domain.LoginMethod
import es.cpinedo.kotlinbase.security.domain.TokenData
import es.cpinedo.kotlinbase.security.infrastructure.configuration.SecurityConfigurationValues
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
import kotlin.collections.ArrayList

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

    override fun generateTokenFromEmail(email: String, loginMethod: LoginMethod): JwtToken {
        val userDetails = userDetailsServiceImpl.loadUserByUsername(email)
        val roles = userDetails.authorities.map { ga -> ga.authority }
        return buildToken(
            alias = userDetails.username,
                    email = email,
                    loginMethod = loginMethod,
                    grantedAuthorities = roles.toTypedArray(),
            )
    }

    override fun getUserNameFromJwtToken(token: String?): String {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).body.subject
    }

    override fun getEmailFromExpiredJwtToken(token: String): String {
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

    override fun buildToken(
        alias: String,
        email: String,
        loginMethod: LoginMethod,
        grantedAuthorities: Array<String>
    ): JwtToken {
        val signingKey: Key =createSigningKey(secret)

        val expirationTime = Date(Date().time + configurationValues.jwtExpirationMs)

        return JwtToken(
            Jwts.builder()
                .setSubject(email)
                .setIssuer(configurationValues.jwtIssuer)
                .setIssuedAt(Date())
                .setExpiration(expirationTime)
                .claim("authorities", grantedAuthorities)
                .claim("loginMethod", loginMethod.name)
                .claim("alias", alias)
                .signWith(SignatureAlgorithm.HS512, signingKey)
                .compact(), expirationTime.time
        )
    }

    override fun getTokenData(token: String, ignoreExpired: Boolean): TokenData {
        val body = try {
            Jwts.parser().setSigningKey(createSigningKey(secret)).parseClaimsJws(token).body
        } catch (e: ExpiredJwtException) {
            logger.info("token expired, ignoreExpired=$ignoreExpired")
            if(!ignoreExpired)
                throw e
            else
                e.claims
        }

        return with(body) {
            TokenData(
                this["sub"] as String,
                this["alias"] as String,
                LoginMethod.valueOf(this["loginMethod"] as String),
                (this["authorities"] as ArrayList<String>).toList(),
            )
        }
    }

    private fun createSigningKey(secret: String):Key{
        val apiKeySecretBytes =
            DatatypeConverter.parseBase64Binary(secret)
        return SecretKeySpec(apiKeySecretBytes, SignatureAlgorithm.HS512.jcaName)
    }

    class TokenNotExpiredYetException : CoreException("The token didn't expire yet", ErrorType.CONFLICT)


}
