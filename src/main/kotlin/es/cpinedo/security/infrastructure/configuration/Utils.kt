package es.cpinedo.security.infrastructure.configuration

import es.cpinedo.core.domain.Either
import org.springframework.util.StringUtils

object Utils {
    fun extractJwtFromAuthHeader(headerAuth: String): Either<String, String> {
        if (!StringUtils.hasText(headerAuth) || !headerAuth.startsWith("Bearer ")) {
            return Either.Left("Bearer not found")
        }
        return Either.Right(headerAuth.substring(7, headerAuth.length))
    }
}