package es.cpinedo.kotlinbase.security.application

import es.cpinedo.kotlinbase.core.domain.Handler
import es.cpinedo.kotlinbase.security.application.ports.AuthenticatedUserService
import es.cpinedo.kotlinbase.security.application.ports.UserService

class DeleteAccountHandler(val userService: UserService, val authenticatedUserService: AuthenticatedUserService) :
    Handler<Unit, DeleteAccountCommand> {
    override fun invoke(request: DeleteAccountCommand) {
        val user = authenticatedUserService.getAuthenticatedUser()
        userService.deleteUser(user.id)
    }
}