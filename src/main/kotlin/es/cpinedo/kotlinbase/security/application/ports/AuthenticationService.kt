package es.cpinedo.kotlinbase.security.application.ports

import es.cpinedo.kotlinbase.security.domain.SecurityUserEntity


interface AuthenticationService {
    fun performAuthentication(user: String, password: String): SecurityUserEntity
}