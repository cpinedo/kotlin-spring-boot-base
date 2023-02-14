package es.cpinedo.security.application.ports

import es.cpinedo.security.domain.SecurityUserEntity


interface AuthenticationService {
    fun performAuthentication(user: String, password: String): SecurityUserEntity
}