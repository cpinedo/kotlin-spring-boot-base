package es.cpinedo.kotlinbase.security.application.ports

import es.cpinedo.kotlinbase.security.application.data.UserData
import java.util.*

interface UserService {
    fun findUserById(id: UUID): UserData
    fun aliasInUse(alias: String): Boolean
    fun emailInUse(email: String): Boolean
    fun save(user: UserData, password: String): UserData
    fun doesEmailExist(mail: String): Boolean
    fun changePassword(id: UUID, newPassword: String)
    fun findByEmail(mail: String): UserData?
    fun deleteUser(id: UUID)
    fun verifyPassword(id: UUID, currentPassword: String)
}