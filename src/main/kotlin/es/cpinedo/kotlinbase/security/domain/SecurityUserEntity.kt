package es.cpinedo.kotlinbase.security.domain

import es.cpinedo.kotlinbase.core.domain.UUIDIdentifier
import es.cpinedo.kotlinbase.security.domain.valueobjects.Password
import es.cpinedo.kotlinbase.security.domain.valueobjects.Username

data class SecurityUserEntity(
    val id: UUIDIdentifier,
    val username: Username,
    val email: String,
    val password: Password,
    val roles: Set<String>,
    val erased: Boolean
)