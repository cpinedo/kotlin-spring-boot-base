package es.cpinedo.security.infrastructure.adapters

import es.cpinedo.security.application.data.UserData
import es.cpinedo.security.application.ports.AuthenticatedUserService
import es.cpinedo.security.application.ports.UserService
import es.cpinedo.security.infrastructure.configuration.UserDetailsImpl
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class AuthenticatedUserServiceImplementation(val userService: UserService) : AuthenticatedUserService {
    override fun getAuthenticatedUser(): UserData {
        val authentication: Authentication = SecurityContextHolder.getContext().authentication
        if(authentication.principal.equals("anonymousUser")) throw UnexpectedAuthenticationException("User not authenticated")
        return userService.findUserById((authentication.principal as UserDetailsImpl).getId())
    }
}

class UnexpectedAuthenticationException(msg: String?) : AuthenticationException(msg)