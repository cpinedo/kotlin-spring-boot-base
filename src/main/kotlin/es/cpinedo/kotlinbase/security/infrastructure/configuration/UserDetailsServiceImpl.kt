package es.cpinedo.kotlinbase.security.infrastructure.configuration

import es.cpinedo.kotlinbase.core.domain.CoreException
import es.cpinedo.kotlinbase.core.domain.ErrorType
import es.cpinedo.kotlinbase.security.infrastructure.repository.SecurityUserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service


@Service
class UserDetailsServiceImpl(val securityUserRepository: SecurityUserRepository) : UserDetailsService {
    override fun loadUserByUsername(email: String): UserDetails {
        val userEntity = securityUserRepository.findByEmail(email) ?: throw UserNotFoundException("User not found $email")
        return UserDetailsImpl(userEntity)
    }

    class UserNotFoundException(message:String) : CoreException(message, ErrorType.NOT_FOUND)
}
