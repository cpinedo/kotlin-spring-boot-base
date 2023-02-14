package es.cpinedo.kotlinbase.security.infrastructure.configuration

import es.cpinedo.kotlinbase.core.domain.UUIDIdentifier
import es.cpinedo.kotlinbase.security.domain.valueobjects.Password
import es.cpinedo.kotlinbase.security.domain.SecurityUserEntity
import es.cpinedo.kotlinbase.security.domain.valueobjects.Username
import es.cpinedo.kotlinbase.security.infrastructure.repository.model.SecurityUserDbEntity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

data class UserDetailsImpl(val securityUserDbEntity: SecurityUserDbEntity) : UserDetails {

    fun getId(): UUID = securityUserDbEntity.id

    fun getEmail(): String = securityUserDbEntity.email

    override fun getAuthorities(): Collection<GrantedAuthority> = securityUserDbEntity.roles
        .map { role -> SimpleGrantedAuthority(role.role.name) }
        .toList()

    override fun getPassword(): String = securityUserDbEntity.password

    override fun getUsername(): String = securityUserDbEntity.username

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = !securityUserDbEntity.erased

    fun toUserEntity(): SecurityUserEntity {
        val roles: Set<String> = this.authorities.map { ga -> ga.authority.toString() }.toSet()
        return SecurityUserEntity(UUIDIdentifier(this.getId()), Username(username), this.getEmail(), Password(password), roles, !this.isEnabled)
    }

}