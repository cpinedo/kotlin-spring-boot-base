package es.cpinedo.kotlinbase.security.application.ports

import es.cpinedo.kotlinbase.security.application.data.UserData

interface AuthenticatedUserService {
    fun getAuthenticatedUser(): UserData
}