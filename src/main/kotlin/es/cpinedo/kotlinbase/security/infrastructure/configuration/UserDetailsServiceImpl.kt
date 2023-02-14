package es.cpinedo.kotlinbase.security.infrastructure.configuration

import es.cpinedo.kotlinbase.core.domain.CoreException
import es.cpinedo.kotlinbase.core.domain.ErrorType
import es.cpinedo.kotlinbase.security.infrastructure.repository.SecurityUserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service


@Service
class UserDetailsServiceImpl(val securityUserRepository: SecurityUserRepository) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val userEntity = securityUserRepository.findByUsername(username) ?: throw UsernameNotFoundException("User not found $username")
        return UserDetailsImpl(userEntity)
    }

    class UsernameNotFoundException(message:String) : CoreException(message, ErrorType.NOT_FOUND)
}
