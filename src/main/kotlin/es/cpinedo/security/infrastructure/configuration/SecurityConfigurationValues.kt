package es.cpinedo.security.infrastructure.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class SecurityConfigurationValues {
    @Value("\${security.jwt.jwtRefreshExpirationMs}")
    val refreshTokenDurationMs: Long = 0

    @Value("\${security.jwt.jwtSecret}")
    val jwtRawSecret: String? = null

    @Value("\${security.jwt.jwtIssuerName}")
    val jwtIssuer: String? = null

    @Value("\${security.jwt.jwtExpirationMs}")
    val jwtExpirationMs: Long = 0

    @Value("\${security.jwt.sessionDurationMs}")
    val sessionDurationMs: Long = 0

}