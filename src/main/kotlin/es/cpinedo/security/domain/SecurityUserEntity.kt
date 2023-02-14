package es.cpinedo.security.domain

import es.cpinedo.core.domain.UUIDIdentifier
import es.cpinedo.security.domain.valueobjects.Password
import es.cpinedo.security.domain.valueobjects.Username

data class SecurityUserEntity(
    val id: UUIDIdentifier,
    val username: Username,
    val email: String,
    val password: Password,
    val roles: Set<String>,
    val erased: Boolean
)