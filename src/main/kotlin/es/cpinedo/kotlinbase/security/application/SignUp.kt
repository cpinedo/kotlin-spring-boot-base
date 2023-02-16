package es.cpinedo.kotlinbase.security.application

import es.cpinedo.kotlinbase.core.domain.CommandQueryBus
import es.cpinedo.kotlinbase.core.domain.CoreException
import es.cpinedo.kotlinbase.core.domain.ErrorType
import es.cpinedo.kotlinbase.core.domain.Handler
import es.cpinedo.kotlinbase.security.application.data.UserData
import es.cpinedo.kotlinbase.security.application.ports.UserService
import java.util.*

class SignUpHandler(
    private val userService: UserService,
    private val commandQueryBus: CommandQueryBus
) : Handler<Unit, SignUpCommand> {
    object Constants {
        const val PASSWORD_MIN_LENGTH: Int = 6
    }

    override fun invoke(request: SignUpCommand) {
        if (request.password.length < Constants.PASSWORD_MIN_LENGTH)
            throw PasswordTooShortException(Constants.PASSWORD_MIN_LENGTH)

        if (userService.aliasInUse(request.alias))
            throw FieldAlreadyInUseException("alias")

        if (userService.emailInUse(request.email))
            throw FieldAlreadyInUseException("mail")

        val strRoles: Set<String> = request.roles

        val userId = UUID.randomUUID()
        val user = UserData(
            id=userId,
            alias=request.alias,
            email=request.email,
            roles=strRoles,
            erased=false,
        )

        userService.save(user, request.password)

        commandQueryBus.dispatch(CreateUserCommand(userId))
    }

    class FieldAlreadyInUseException(field: String) : CoreException("$field already in use!", ErrorType.CONFLICT)
    class UserTooShortException(length: Int) : CoreException("The user minimum length is $length", ErrorType.CONFLICT)
    class PasswordTooShortException(length: Int) :
        CoreException("The password minimum length $length", ErrorType.CONFLICT)

    class RoleDoesNotExistException(roleName: String) :
        CoreException("$roleName role doesn't exist!", ErrorType.BAD_REQUEST)
}