package es.cpinedo.kotlinbase.security.application

import es.cpinedo.kotlinbase.core.domain.Command
import es.cpinedo.kotlinbase.core.domain.Query
import java.util.*

data class RefreshTokenQuery(val refreshToken: String, val token: String) : Query<RefreshTokenResponse>
data class SignUpCommand(
    val alias: String,
    val password: String,
    val email: String,
    val roles: Set<String>) :
    Command<Unit> {
    override fun toString(): String {
        return "SignUpCommand(alias='$alias', password='*****', email='$email', roles=$roles)"
    }
}
data class LoginQuery(val email: String, val password: String) : Query<LoginResponse> {
    override fun toString(): String {
        return "LoginQuery(email='$email', password='*****')"
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
data class LoginWithGoogleQuery(val idToken: String): Query<LoginResponse>