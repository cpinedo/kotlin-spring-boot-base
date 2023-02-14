package es.cpinedo.security.application.ports

import es.cpinedo.security.application.data.UserData

interface AuthenticatedUserService {
    fun getAuthenticatedUser(): UserData
}