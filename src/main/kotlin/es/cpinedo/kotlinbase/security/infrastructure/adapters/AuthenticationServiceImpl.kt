package es.cpinedo.kotlinbase.security.infrastructure.adapters

import es.cpinedo.kotlinbase.security.application.ports.AuthenticationService
import es.cpinedo.kotlinbase.security.domain.SecurityUserEntity
import es.cpinedo.kotlinbase.security.infrastructure.configuration.UserDetailsImpl
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class AuthenticationServiceImpl(
    private val authenticationManager: AuthenticationManager
) : AuthenticationService {
    override fun performAuthentication(user: String, password: String): SecurityUserEntity {
        val authentication: Authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(user, password)
        )
        SecurityContextHolder.getContext().authentication = authentication

        return (authentication.principal as UserDetailsImpl).toUserEntity()
    }
}