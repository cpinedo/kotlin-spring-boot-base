package es.cpinedo.kotlinbase.security.infrastructure.configuration

import es.cpinedo.kotlinbase.core.domain.Either
import es.cpinedo.kotlinbase.security.application.ports.JwtUtils
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AuthTokenFilter(
    private val jwtUtils: JwtUtils,
    private val userDetailsService: UserDetailsService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        when (val jwt: Either<String, String> = parseJwt(request)) {
            is Either.Right -> {
                try {
                    if (jwtUtils.validateJwtToken(jwt.r)) {
                        val username: String = jwtUtils.getUserNameFromJwtToken(jwt.r)
                        val userDetails = userDetailsService.loadUserByUsername(username)
                        val authentication = UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.authorities
                        )
                        authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                        SecurityContextHolder.getContext().authentication = authentication
                    }
                } catch (e: Exception) {
                    logger.error("Cannot set user authentication: {}", e)
                }
            }
            is Either.Left -> logger.trace(jwt.l)
        }
        filterChain.doFilter(request, response)
    }

    private fun parseJwt(request: HttpServletRequest): Either<String, String> {
        val headerAuth = request.getHeader("Authorization") ?: return Either.Left("No Authorization header")
        return Utils.extractJwtFromAuthHeader(headerAuth)
    }

}
