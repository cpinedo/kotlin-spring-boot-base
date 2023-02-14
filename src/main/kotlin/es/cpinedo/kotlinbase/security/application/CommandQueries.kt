package es.cpinedo.kotlinbase.security.application

import es.cpinedo.kotlinbase.core.domain.Command
import es.cpinedo.kotlinbase.core.domain.Query
import java.util.*

data class RefreshTokenQuery(val refreshToken: String, val token: String) : Query<RefreshTokenResponse>
data class SignUpCommand(val username: String, val password: String, val email: String, val roles: Set<String>) :
    Command<Unit> {
    override fun toString(): String {
        return "SignUpCommand(username='$username', password='*****', email='$email', roles=$roles)"
    }
}
data class LoginQuery(val user: String, val password: String) : Query<LoginResponse> {
    override fun toString(): String {
        return "LoginQuery(user='$user', password='*****')"
    }
}
data class RecoverPasswordCommand(val mail: String) : Command<Unit>
data class ChangePasswordCommand(val newPassword: String, val currentPassword: String) : Command<Unit> {
    override fun toString(): String {
        return "ChangePasswordCommand(newPassword='*****', currentPassword='*****')"
    }
}
data class ChangePasswordInternalCommand(val newPassword: String, val userId: UUID) : Command<Unit> {
    override fun toString(): String {
        return "ChangePasswordInternalCommand(newPassword='*****', userId=$userId)"
    }
}
class DeleteAccountCommand : Command<Unit>
data class CreateUserCommand(val userId: UUID) : Command<Unit>