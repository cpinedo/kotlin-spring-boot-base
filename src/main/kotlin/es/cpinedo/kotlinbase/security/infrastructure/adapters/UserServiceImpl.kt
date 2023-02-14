package es.cpinedo.kotlinbase.security.infrastructure.adapters

import es.cpinedo.kotlinbase.security.application.SignUpHandler
import es.cpinedo.kotlinbase.security.application.data.UserData
import es.cpinedo.kotlinbase.security.application.ports.AuthenticationService
import es.cpinedo.kotlinbase.security.application.ports.UserService
import es.cpinedo.kotlinbase.security.infrastructure.repository.RoleRepository
import es.cpinedo.kotlinbase.security.infrastructure.repository.SecurityUserRepository
import es.cpinedo.kotlinbase.security.infrastructure.repository.model.SecurityUserDbEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserServiceImpl(
    private val securityUserRepository: SecurityUserRepository,
    private val roleRepository: RoleRepository,
    private val encoder: PasswordEncoder,
    private val authenticationService: AuthenticationService
) : UserService {
    override fun findUserById(id: UUID): UserData {
        return securityUserRepository.findById(id).let { it.get().toUserData() }
    }

    override fun userNameInUse(username: String): Boolean {
        return securityUserRepository.existsByUsername(username)
    }

    override fun emailInUse(email: String): Boolean {
        return securityUserRepository.existsByEmail(email)
    }

    override fun save(user: UserData, password: String): UserData {
        val roles = user.roles.map { roleName: String ->
            roleRepository.findByName(roleName) ?: throw SignUpHandler.RoleDoesNotExistException(roleName)
        }.toHashSet()

        val securityUserDbEntityEntity = SecurityUserDbEntity(
            user.id,
            user.username,
            user.email,
            encoder.encode(password),
            user.erased,
            roles
        )

        return securityUserRepository.save(securityUserDbEntityEntity).toUserData()
    }

    override fun doesEmailExist(mail: String): Boolean {
        return securityUserRepository.existsByEmail(mail)
    }

    override fun findByEmail(mail: String): UserData? {
        return securityUserRepository.findByEmail(mail)?.toUserData()
    }

    override fun deleteUser(id: UUID) {
        securityUserRepository.findById(id).get().let{
            it.erased = true
            securityUserRepository.save(it)
        }
    }

    override fun changePassword(id: UUID, newPassword: String) {
        securityUserRepository.findById(id).ifPresent {
            it.password = encoder.encode(newPassword)
            securityUserRepository.save(it)
        }
    }

    override fun verifyPassword(id: UUID, currentPassword: String) {
        authenticationService.performAuthentication(
            securityUserRepository.findById(id).get().username,
            currentPassword
        )
    }

}
