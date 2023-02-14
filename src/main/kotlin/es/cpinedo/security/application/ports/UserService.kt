package es.cpinedo.security.application.ports

import es.cpinedo.security.application.data.UserData
import java.util.*

interface UserService {
    fun findUserById(id: UUID): UserData
    fun userNameInUse(username: String): Boolean
    fun emailInUse(email: String): Boolean
    fun save(user: UserData, password: String): UserData
    fun doesEmailExist(mail: String): Boolean
    fun changePassword(id: UUID, newPassword: String)
    fun findByEmail(mail: String): UserData?
    fun deleteUser(id: UUID)
    fun verifyPassword(id: UUID, currentPassword: String)
}